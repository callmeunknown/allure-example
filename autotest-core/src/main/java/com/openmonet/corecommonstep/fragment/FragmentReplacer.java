package com.openmonet.corecommonstep.fragment;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.FeatureWithLines;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.gherkin.Step;
import io.cucumber.gherkin.GherkinDialect;
import io.cucumber.gherkin.GherkinDialectProvider;
import io.cucumber.gherkin.IGherkinDialectProvider;
import io.cucumber.messages.Messages;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.DocStringArgument;
import io.cucumber.plugin.event.Location;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.asserts.Assertion;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openmonet.utils.ErrorMessage.*;
import static com.openmonet.utils.FileUtil.getParentPath;
import static com.openmonet.utils.RegexUtil.getMatch;
import static com.openmonet.utils.RegexUtil.getMatchValueByGroupNumber;

public class FragmentReplacer {

    public static final String REGEX_FRAGMENT = "^insert fragment \"(.+)\"$";
    public static final String REGEX_FRAGMENT_SPOILER = "FRAGMENT \"(.+)\"";
    public static final String FEATURE_SUFFIX = ".feature";
    public static final String FRAGMENT_TAG = "@fragment";
    private static final String REGEX_VALUE = "<%s>";
    private static final String REGEX_EXAMPLE = "<(.*)>";

    private List<Feature> features;
    private Map<Pickle, String> scenarioLanguageMap;
    private MutableValueGraph<Object, String> fragmentsGraph;
    private Assertion assertion;

    public FragmentReplacer(List<Feature> features, Assertion assertion) throws IOException {
        this.assertion = assertion;
        this.features = cacheFragmentsToFeatures(features);
        this.scenarioLanguageMap = cacheScenarioLanguage(this.features);
        Map<String, Pickle> fragmentsMap = cacheFragmentsAsMap(this.features);
        this.fragmentsGraph = cacheFragmentsAsGraph(this.features, fragmentsMap, this.scenarioLanguageMap);
    }

    public void replace() throws IllegalAccessException {
        while (!fragmentsGraph.edges().isEmpty()) {
            int fragmentsGraphSize = fragmentsGraph.edges().size();

            for (EndpointPair edge : new ArrayList<>(fragmentsGraph.edges())) {
                Pickle fragment = (Pickle) edge.nodeV();
                Pickle scenario = (Pickle) edge.nodeU();

                if (isTerminal(fragment)) {
                    replaceFragmentInScenario(scenario, fragment);
                    fragmentsGraph.removeEdge(scenario, fragment);
                }
            }

            if (fragmentsGraphSize == fragmentsGraph.edges().size()) {
                throw new AssertionError("Fragments replacing is no longer performed, it will lead to an infinite loop. Interrupting...");
            }
        }
    }

    /**
     * загрузка фича файлов из папки с фрагментами
     *
     * @return лист Feature файлов
     * @throws IOException
     */
    private List<Feature> cacheFragmentsToFeatures(List<Feature> features) throws IOException {
        List<FeatureWithLines> featurePaths = new ArrayList();
        StringBuilder pathBuilder = getParentPath("fragments");

        List<Path> collect = Files.walk(Paths.get(pathBuilder.toString()))
                .filter(p -> p.toAbsolutePath().toString().endsWith(FEATURE_SUFFIX))
                .collect(Collectors.toList());
        collect.forEach(p -> {
            featurePaths.add(FeatureWithLines.create(p.toUri(), Collections.emptyList()));
        });

        List<URI> uriList = featurePaths.stream().map(p -> p.uri()).collect(Collectors.toList());
        List<Feature> loadedFeaturesList = new ArrayList<>();
        FeatureParser featureParser = new FeatureParser(() -> UUID.randomUUID());

        Iterator<URI> iterator = uriList.iterator();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            StringBuilder stringBuilder = new StringBuilder();
            Files.readAllLines(Paths.get(uri)).forEach(p -> stringBuilder.append(p).append("\n"));
            GherkinResource gherkinResource = new GherkinResource(stringBuilder.toString(), uri);
            Optional<Feature> feature = featureParser.parseResource(gherkinResource);
            feature.ifPresent(loadedFeaturesList::add);
        }

        return Stream.concat(features.stream(), loadedFeaturesList.stream()).collect(Collectors.toList());
    }

    private Map<Pickle, String> cacheScenarioLanguage(List<Feature> features) {
        Map<Pickle, String> scenarioLanguageMap = new HashMap<>();
        for (Feature feature : features) {
            List<Pickle> pickles = feature.getPickles();
            for (Pickle pickle : pickles) {
                scenarioLanguageMap.put(pickle, pickle.getLanguage());
            }
        }
        return scenarioLanguageMap;
    }

    private Map<String, Pickle> cacheFragmentsAsMap(List<Feature> features) {
        Map<String, Pickle> fragments = new HashMap<>();

        List<Pickle> collect = features.stream().map(Feature::getPickles).flatMap(List::stream).collect(Collectors.toList());

        for (Pickle pickle : collect) {
            if (isFragmentTagContains(pickle.getTags())) {
                List<Pickle> pickles = collect.stream()
                        .filter(p -> isFragmentTagContains(p.getTags()))
                        .filter(p -> p.getName().equalsIgnoreCase(pickle.getName()))
                        .collect(Collectors.toList());
                if (pickles.size() > 1) {
                    StringBuilder stringBuilder = new StringBuilder();
                    pickles.forEach(p -> stringBuilder.append(p.getName()).append(", ").append(p.getUri().getPath()).append(";\n"));
                    assertion.fail(getErrorMessage(FOUND_MORE_ONE_FRAGMENT, stringBuilder.toString()));
                }
            }
        }

        for (Feature feature : features) {
            List<Pickle> pickles = feature.getPickles();
            for (Pickle pickle : pickles) {
                List<String> tags = pickle.getTags();
                if (isFragmentTagContains(tags)) {
                    fragments.put(pickle.getName(), pickle);
                }
            }
        }

        return fragments;
    }

    private MutableValueGraph<Object, String> cacheFragmentsAsGraph(List<Feature> features,
                                                                    Map<String, Pickle> fragmentsMap,
                                                                    Map<Pickle, String> scenarioLanguageMap) {
        MutableValueGraph<Object, String> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();

        for (Feature feature : features) {
            List<Pickle> pickleList = feature.getPickles().stream().filter(pickle1 -> isScenario(pickle1)).collect(Collectors.toList());

            for (Pickle pickle : pickleList) {
                addGraphNode(graph, pickle, fragmentsMap, scenarioLanguageMap);
            }
        }

        return graph;
    }

    private void addGraphNode(MutableValueGraph graph,
                              Pickle scenario,
                              Map<String, Pickle> fragmentsMap,
                              Map<Pickle, String> scenarioLanguageMap) {
        graph.addNode(scenario);
        String scenarioLanguage = scenarioLanguageMap.get(scenario);
        List<Step> steps = scenario.getSteps();

        for (Step step : steps) {
            if (getMatch(step.getText(), REGEX_FRAGMENT_SPOILER)) {
                assertion.fail(String.format("Для использования фрагментов используйте другой шаг: %s\n Путь к фича-файлу: %s", REGEX_FRAGMENT, scenario.getUri().getPath()));
            }
            String fragmentName = getFragmentName(step);
            if (fragmentName != null) {
                String fragmentNameFromMap = fragmentsMap.keySet().stream()
                        .filter(key -> key.equalsIgnoreCase(fragmentName))
                        .findAny().orElse(null);
                Pickle pickle = fragmentsMap.get(fragmentNameFromMap);

                if (pickle == null) {
                    // пофиг на данные @data
                    assertion.fail(getErrorMessage(FRAGMENT_NOT_EXIST, fragmentName));
                } else {
                    String fragmentLanguage = pickle.getLanguage();
                    assertion.assertEquals(scenarioLanguage, fragmentLanguage, getErrorMessage(LANGUAGE_IS_NOT_SAME, scenarioLanguage, fragmentLanguage));

                    graph.putEdgeValue(scenario, pickle, "");

                    addGraphNode(graph, pickle, fragmentsMap, scenarioLanguageMap);
                }
            }
        }
    }

    private boolean isFragmentTagContains(List<String> tags) {
        return tags.stream().anyMatch(tag -> tag.equals(FRAGMENT_TAG));
    }

    private boolean isScenario(Pickle pickle) {
        List<String> tags = pickle.getTags();
        return tags.stream().noneMatch(tag -> tag.equals(FRAGMENT_TAG));
    }

    private boolean isTerminal(Object node) {
        for (EndpointPair edge : fragmentsGraph.edges()) {
            if (edge.nodeU().equals(node)) {
                return false;
            }
        }

        return true;
    }

    private void replaceFragmentInScenario(Pickle scenario, Pickle fragment) throws IllegalAccessException {
        List<Step> replacementSteps = new ArrayList<>();

        for (Step step : scenario.getSteps()) {
            String fragmentName = getFragmentName(step);
            if (fragmentName != null && !fragmentName.isEmpty() && fragmentName.equalsIgnoreCase(fragment.getName())) {
                Step mockStep = getMockStep(step, fragment);
                replacementSteps.add(mockStep);
                replacementSteps.addAll(replaceSteps(step, fragment.getSteps(), fragment.getLanguage(), scenario));
                replacementSteps.add(mockStep);
            } else {
                replacementSteps.add(step);
            }
        }
        FieldUtils.writeField(scenario, "steps", replacementSteps, true);
    }

    /**
     * Генерирует шаг-заглушки, который в аллюр отчете подменяется на спойлер
     * @param step  -   шаг "вызвать фрагмент"
     * @param fragment  -   сценарий-фрагмент
     * @return  -   шаг ФРАГМЕНТ "название фрагмента"
     */
    private Step getMockStep(Step step, Pickle fragment) {
        IGherkinDialectProvider dialectProvider = new GherkinDialectProvider();
        GherkinDialect dialect = dialectProvider.getDialect(fragment.getLanguage(), null);
        Location location = step.getLocation();
        return new CustomStep(
                step.getId(),
                String.format("FRAGMENT \"%s\"", fragment.getName()),
                new ArrayList<>(),
                Object.class,
                dialect,
                step.getPreviousGivenWhenThenKeyword(),
                Messages.Location.newBuilder().setLine(location.getLine()).setColumn(location.getColumn()).build(),
                step.getKeyword());
    }

    private String getFragmentName(Step step) {
        return getMatchValueByGroupNumber(step.getText(), REGEX_FRAGMENT, 1);
    }

    private List<Step> replaceSteps(Step scenarioStep, List<Step> fragmentsSteps, String language, Pickle scenario) {
        DataTableArgument argument = (DataTableArgument) scenarioStep.getArgument();
        List<Step> replacementSteps = new ArrayList<>();

        if (argument != null) {
            Class argumentType = Object.class;
            List<List<String>> lines = argument.cells();
            checkArguments(lines, scenario);
            for (Step fragmentStep : fragmentsSteps) {
                List<List<String>> replaceFragmentLines = new ArrayList<>();
                if (fragmentStep.getArgument() instanceof DataTableArgument) {
                    argumentType = DataTableArgument.class;
                    /* замена данных в таблице если она есть у шага */
                    DataTableArgument fragmentStepArgument = (DataTableArgument) fragmentStep.getArgument();
                    if (fragmentStepArgument != null) {
                        for (List<String> fragmentLines : fragmentStepArgument.cells()) {
                            for (List<String> line : lines) {
                                String regex = String.format(REGEX_VALUE, line.get(0));
                                fragmentLines = fragmentLines.stream().map(p -> p.replace(regex, line.get(1))).collect(Collectors.toList());
                            }
                            StringBuilder values = new StringBuilder();
                            for (String fragmentLine : fragmentLines) {
                                if (getMatch(fragmentLine, REGEX_EXAMPLE)) {
                                    values.append(getMatchValueByGroupNumber(fragmentLine, REGEX_EXAMPLE, 1)).append(" ");
                                }
                            }
                            if (values.length() != 0) {
                                assertion.fail(
                                        getErrorMessage(NOT_FOUND_VARS_FOR_DYNAMIC_FRAGMENTS,
                                                scenario.getName(),
                                                fragmentStep.getText(),
                                                values.toString()));
                            }
                            replaceFragmentLines.add(fragmentLines);
                        }
                    }
                } else if (fragmentStep.getArgument() instanceof DocStringArgument) {
                    argumentType = DocStringArgument.class;
                    /* замена данных в DocStringArgument если есть у шага */
                    DocStringArgument fragmentStepArgument = (DocStringArgument) fragmentStep.getArgument();
                    if (fragmentStepArgument != null) {
                        String content = fragmentStepArgument.getContent();
                        for (List<String> line : lines) {
                            String regex = String.format(REGEX_VALUE, line.get(0));
                            content = content.replace(regex, line.get(1));
                        }
                        checkVarsForDynamicFragments(content, scenario, fragmentStep);
                        replaceFragmentLines.add(Collections.singletonList(content));
                    }
                }

                /* замена в текстовке шага */
                String replaceValue = fragmentStep.getText();
                for (List<String> line : lines) {
                    String regex = String.format(REGEX_VALUE, line.get(0));
                    if (getMatch(fragmentStep.getText(), regex)) {
                        replaceValue = replaceValue.replace(regex, line.get(1));
                    }
                }
                checkVarsForDynamicFragments(replaceValue, scenario, fragmentStep);

                IGherkinDialectProvider dialectProvider = new GherkinDialectProvider();
                GherkinDialect dialect = dialectProvider.getDialect(language, null);
                Location location = fragmentStep.getLocation();
                Step replaceStep = new com.openmonet.corecommonstep.fragment.CustomStep(
                        fragmentStep.getId(),
                        replaceValue,
                        replaceFragmentLines,
                        argumentType,
                        dialect,
                        fragmentStep.getPreviousGivenWhenThenKeyword(),
                        Messages.Location.newBuilder().setLine(location.getLine()).setColumn(location.getColumn()).build(),
                        fragmentStep.getKeyword());
                replacementSteps.add(replaceStep);
            }

            return replacementSteps;
        } else {
            return fragmentsSteps;
        }
    }

    private void checkArguments(List<List<String>> lines, Pickle scenario) {
        lines.forEach(line ->
                assertion.assertEquals(
                        line.size(),
                        2,
                        String.format("Количество значений в таблице не равно двум. Актуальное значение: %s. Путь '%s', наименование сценария '%s'.",
                                line.size(),
                                scenario.getUri().getPath(),
                                scenario.getName()
                        )
                )
        );
    }

    private void checkVarsForDynamicFragments(String value, Pickle scenario, Step fragmentStep) {
        if (getMatch(value, REGEX_EXAMPLE)) {
            assertion.fail(
                    getErrorMessage(NOT_FOUND_VARS_FOR_DYNAMIC_FRAGMENTS,
                            scenario.getName(),
                            fragmentStep.getText(),
                            getMatchValueByGroupNumber(value, REGEX_EXAMPLE, 1)));
        }
    }
}
