package com.uniovi.sercheduler.commands;

import com.uniovi.sercheduler.parser.HostLoader;
import com.uniovi.sercheduler.parser.WorkflowLoader;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

/** Contains all commands related to the execution of the GA. */
@Command
public class EvaluateCommand {

  static final Logger LOG = LoggerFactory.getLogger(EvaluateCommand.class);

  final WorkflowLoader workflowLoader;
  final HostLoader hostLoader;

  public EvaluateCommand(WorkflowLoader workflowLoader, HostLoader hostLoader) {
    this.workflowLoader = workflowLoader;
    this.hostLoader = hostLoader;
  }

  /**
   * Command to run the GA for a given instance an infrastructure.
   *
   * @param hostsFile Relative or Absolute path to the hosts file.
   * @param workflowFile Relative or Absolute path to the workflow file.
   * @return The text to print at the end.
   */
  @Command(command = "evaluate")
  public String evaluate(
      @Option(shortNames = 'H', required = true) String hostsFile,
      @Option(shortNames = 'W', required = true) String workflowFile) {
    Instant start = Instant.now();

    LOG.info("Loading {} host file", hostsFile);
    var hosts = hostLoader.load(hostLoader.readFromFile(new File(hostsFile)));
    LOG.info("Loaded hosts with {} hosts", hosts.size());

    LOG.info("Loading {} workflow file", workflowFile);
    var workflow = workflowLoader.load(workflowLoader.readFromFile(new File(workflowFile)));
    LOG.info("Loaded workflow with {} tasks", workflow.size());

    Instant finish = Instant.now();

    var timeElapsed = Duration.between(start, finish);
    return String.format("Evaluation done, it took %d", timeElapsed.getSeconds());
  }
}
