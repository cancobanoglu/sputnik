package pl.touk.sputnik.cli;

import lombok.Getter;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.Connectors;

public class CliWrapper {
    public static final String CONF = "conf";
    public static final String CHANGE_ID = "changeId";
    public static final String REVISION_ID = "revisionId";
    public static final String CONNECTOR = "connector";
    public static final String PULL_REQUEST_ID = "pullRequestId";

    @Getter
    private final Options options;

    public CliWrapper() {
        options = createOptions();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private Options createOptions() {
        Options options = new Options();
        options.addOption(buildOption(CONF, true, true, "Configuration properties file"));
        options.addOption(buildOption(CONNECTOR, true, true, "Connector: <stash|gerrit>"));

        options.addOption(buildOption(CHANGE_ID, true, false, "Gerrit change id"));
        options.addOption(buildOption(REVISION_ID, true, false, "Gerrit revision id"));

        options.addOption(buildOption(PULL_REQUEST_ID, true, false, "Stash pull request Id"));

        return options;
    }

    @NotNull
    public CommandLine parse(@NotNull String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        return parser.parse(options, args);
    }

    @NotNull
    @SuppressWarnings("all")
    private Option buildOption(@NotNull String name, boolean hasArgs, boolean isRequired, @NotNull String description) {
        return OptionBuilder.withArgName(name)
            .withLongOpt(name)
            .hasArg(hasArgs)
            .isRequired(isRequired)
            .withDescription(description)
            .create();
    }

    public Connectors contextSensitiveValidation(CommandLine cli) throws ParseException {
        Connectors connector = Connectors.valueOf(cli.getOptionValue(CONNECTOR).toUpperCase());
        if (connector == Connectors.GERRIT && cli.hasOption(CHANGE_ID) && cli.hasOption(REVISION_ID)) {
            return connector;
        } else if (connector == Connectors.STASH && cli.hasOption(PULL_REQUEST_ID)) {
            return connector;
        }

        throw new ParseException("CLI arguments out of context");
    }

    public Connectors connector(CommandLine commandLine) {
        return Connectors.valueOf(commandLine.getOptionValue(CONNECTOR).toUpperCase());
    }
}