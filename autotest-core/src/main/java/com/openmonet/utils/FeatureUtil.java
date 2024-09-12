package com.openmonet.utils;

import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.FeatureWithLines;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.FeatureParserException;
import io.cucumber.core.gherkin.Pickle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.testng.asserts.Assertion;
import com.openmonet.corecommonstep.fragment.GherkinResource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.openmonet.corecommonstep.fragment.FragmentReplacer.FEATURE_SUFFIX;
import static com.openmonet.corecommonstep.fragment.FragmentReplacer.FRAGMENT_TAG;
import static com.openmonet.utils.ErrorMessage.FEATURE_PARSE_EXCEPTION;
import static com.openmonet.utils.ErrorMessage.getErrorMessage;

public class FeatureUtil {

    /**
     * загрузка фич из директории, кроме фрагментов
     *
     * @param path путь
     * @return список фич
     * @throws IOException
     */
    public static List<Feature> loadFeature(String path, Assertion assertion, boolean withoutFragments) throws IOException {
        List<FeatureWithLines> featurePaths = new ArrayList();
        List<Path> collect = Files.walk(Paths.get(path))
                .filter(p -> p.toAbsolutePath().toString().endsWith(FEATURE_SUFFIX))
                .collect(Collectors.toList());
        collect.forEach(p -> featurePaths.add(FeatureWithLines.create(
                URI.create("file:" + p.toAbsolutePath().toString()),
                Collections.emptyList()
        )));

        List<URI> uriList = featurePaths.stream().map(p -> p.uri()).collect(Collectors.toList());
        List<Feature> loadedFeaturesList = new ArrayList<>();
        FeatureParser featureParser = new FeatureParser(() -> UUID.randomUUID());

        Iterator<URI> iterator = uriList.iterator();
        while (iterator.hasNext()) {
            URI uri = iterator.next();
            StringBuilder stringBuilder = new StringBuilder();
            Files.readAllLines(Paths.get(uri)).forEach(p -> stringBuilder.append(p).append("\n"));
            GherkinResource gherkinResource = new GherkinResource(stringBuilder.toString(), uri);
            try {
                Optional<Feature> feature = featureParser.parseResource(gherkinResource);
                feature.ifPresent(loadedFeaturesList::add);
            } catch (FeatureParserException e) {
                assertion.fail(getErrorMessage(FEATURE_PARSE_EXCEPTION, ExceptionUtils.getMessage(e)));
            }
        }

        if (withoutFragments) {
            List<Feature> removeFeatures = new ArrayList<>();
            // удалить все фичи с фрагментами
            for (Feature featureWithLines : loadedFeaturesList) {
                List<Pickle> pickles = new ArrayList<>();
                for (Pickle pickle : featureWithLines.getPickles()) {
                    if (pickle.getTags().stream().anyMatch(p -> p.equals(FRAGMENT_TAG))) {
                        pickles.add(pickle);
                    }
                }
                featureWithLines.getPickles().removeAll(pickles);
                if (featureWithLines.getPickles().size() == 0) {
                    removeFeatures.add(featureWithLines);
                }
            }

            for (Feature feature : removeFeatures) {
                loadedFeaturesList.remove(feature);
            }
        }

        return loadedFeaturesList;
    }
}
