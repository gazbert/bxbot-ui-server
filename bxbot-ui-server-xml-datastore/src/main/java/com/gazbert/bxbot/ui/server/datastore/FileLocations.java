package com.gazbert.bxbot.ui.server.datastore;


/**
 * Locations of XML and XSD files for the entities.
 *
 * @author gazbert
 */
public final class FileLocations {

    /*
     * Location of the XML config files relative to project/installation root.
     */
    public static final String STRATEGIES_CONFIG_XML_FILENAME = "config/strategies.xml";
    public static final String BOTS_CONFIG_XML_FILENAME = "config/bots.xml";

    /*
     * XSD schema files for validating the XML config - their location in the main/resources folder.
     */
    public static final String STRATEGIES_CONFIG_XSD_FILENAME = "com/gazbert/bxbot/ui/server/datastore/config/strategies.xsd";
    public static final String BOTS_CONFIG_XSD_FILENAME = "com/gazbert/bxbot/ui/server/datastore/config/bots.xsd";

    private FileLocations() {
    }
}
