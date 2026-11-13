package com.uniovi.sercheduler.service;

import com.uniovi.sercheduler.dto.InstanceData;
import com.uniovi.sercheduler.dto.Task;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/** Contains operations that can be done to an existing plan. */
public class Operators implements Serializable {

  InstanceData instanceData;
  Random random;

  public Operators(InstanceData instanceData) {
    this(instanceData, new Random());
  }

  public Operators(InstanceData instanceData, Random random) {
    this.instanceData = instanceData;
    this.random = random;
  }

  /**
   * Takes two plans and create a new one that merges both by a random position.
   *
   * @param plan1 plan to merge
   * @param plan2 plan to merge
   * @return The merged plan.
   */
  public List<PlanPair> doCrossover(List<PlanPair> plan1, List<PlanPair> plan2) {

    List<PlanPair> newPlan = new ArrayList<>();
    // Choose a random position to do the cut
    int position = random.nextInt(0, plan1.size());

    for (int i = 0; i < position; i++) {
      newPlan.add(plan1.get(i));
    }
    var planSet =
        newPlan.stream().map(PlanPair::task).map(Task::getName).collect(Collectors.toSet());
    for (int i = 0; i < plan2.size() && position < plan1.size(); i++) {
      // We use a set to more efficiently know if the plan already ahs that element

      if (planSet.add(plan2.get(i).task().getName())) {
        newPlan.add(new PlanPair(plan2.get(i).task(), plan2.get(i).host()));
        position++;
      }
    }
    return newPlan;
  }

  /**
   * Creates a new plan selecting a random position and moving the element to a new random position.
   *
   * <p>HAs a chance of modifying the host too.
   *
   * @param plan Original plan to mutate.
   * @return The new plan.
   */
  public List<PlanPair> mutate(List<PlanPair> plan) {

    if (plan.size() < 2) {
      return plan;
    }

    // Choose a random position to mutate
    int position = random.nextInt(0, plan.size());

    // Find the left boundary
    int positionLeft = position - 1;

    while (positionLeft >= 0) {
      if (plan.get(position).task()
              .getParents()
              .contains(plan.get(positionLeft).task())) {
        break;
      }

      positionLeft--;
    }

    // Find the right boundary
    int positionRight = position + 1;

    while (positionRight < plan.size()) {
      if (plan.get(position).task()
              .getChildren()
              .contains(plan.get(positionRight).task())) {
        break;
      }

      positionRight++;
    }

    // Choose a new position within the viable range
    int newPosition = random.nextInt(
            positionLeft + 1,
            positionRight
    );

    // Create a mutable copy
    var newPlan = new ArrayList<>(plan);

    // Move the selected element
    PlanPair selectedPair = newPlan.get(position);

    if (newPosition < position) {

      for (int i = position; i > newPosition; i--) {
        newPlan.set(i, newPlan.get(i - 1));
      }

    } else if (newPosition > position) {

      for (int i = position; i < newPosition; i++) {
        newPlan.set(i, newPlan.get(i + 1));
      }
    }

    // Assign a new host
    var newHost = instanceData.hosts()
            .values()
            .stream()
            .toList()
            .get(random.nextInt(0, instanceData.hosts().size()));

    newPlan.set(
            newPosition,
            new PlanPair(selectedPair.task(), newHost)
    );

    return Collections.unmodifiableList(newPlan);
  }
}
