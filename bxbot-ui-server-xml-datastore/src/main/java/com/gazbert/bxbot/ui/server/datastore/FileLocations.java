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

    /*
     * XSD schema files for validating the XML config - their location in the main/resources folder.
     */
    public static final String STRATEGIES_CONFIG_XSD_FILENAME = "com/gazbert/bxbot/ui/server/ui/server/ui/server/datastore/config/strategies.xsd";


    private FileLocations() {
    }
}
