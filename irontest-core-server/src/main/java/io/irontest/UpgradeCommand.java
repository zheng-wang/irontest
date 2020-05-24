package io.irontest;

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class UpgradeCommand extends Command {

    protected UpgradeCommand() {
        super("upgrade", "Upgrade Iron Test");
    }

    @Override
    public void configure(Subparser subparser) {

    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) {

    }
}
