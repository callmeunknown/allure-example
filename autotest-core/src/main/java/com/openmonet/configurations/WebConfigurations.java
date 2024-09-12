package com.openmonet.configurations;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

@LoadPolicy(LoadType.FIRST)
@Sources({
        "classpath:config/${nameConfig}.properties",
        "classpath:config/chrome.properties",
        "system:properties",
        "system:env"
})
public interface WebConfigurations extends Config {

    @Key("webdriver.browser.size")
    @DefaultValue("1920x1080")
    String webDriverBrowserSize();

    @Key("hub.url")
    @DefaultValue("")
    String hubUrl();

    @Key("selenoid.enableVNC")
    @DefaultValue("false")
    boolean enableVnc();

    @Key("selenoid.enableVideo")
    @DefaultValue("false")
    boolean enableVideo();

    @Key("proxy.enable")
    @DefaultValue("false")
    boolean proxyEnable();

    @Key("proxy.host")
    @DefaultValue("")
    String proxyHost();

    @Key("proxy.port")
    @DefaultValue("")
    Integer proxyPort();

    @Key("webdriver.browser.name")
    @DefaultValue("")
    String webDriverBrowserName();

    @Key("webdriver.timeoutSeconds")
    @DefaultValue("10")
    int webDriverTimeoutSeconds();

    @Key("polling.timeoutMs")
    @DefaultValue("200")
    int pollingTimeoutMs();

    @Key("webdriver.version")
    @DefaultValue("")
    String webDriverVersion();

    @Key("wdm.property")
    @DefaultValue("")
    String wdmProperty();

    @Key("pages.package")
    @DefaultValue("pages")
    String pagesPackage();

    @Key("mobile")
    @DefaultValue("false")
    Boolean isMobile();

    @Key("webdriver.userAgent")
    @DefaultValue("")
    String webDriverUserAgent();

    static WebConfigurations config() {
        return ConfigFactory.create(WebConfigurations.class, System.getProperties(), System.getenv());
    }
}
