package com.uniovi.sercheduler.jmetal.problem;

import static com.uniovi.sercheduler.util.LoadTestInstanceData.loadFitnessTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.uniovi.sercheduler.jmetal.operator.ScheduleCrossover;
import com.uniovi.sercheduler.jmetal.operator.ScheduleMutation;
import com.uniovi.sercheduler.service.Operators;
import com.uniovi.sercheduler.service.PlanPair;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.AbstractSolution;

class SchedulePermutationSolutionTest {

  @Test
  void retainsOnlyThePlanForVariablesInBothConstructors() throws Exception {
    var instance = loadFitnessTest();
    var plan = new ArrayList<>(List.of(
        new PlanPair(instance.workflow().get("task01"), instance.hosts().get("HostA")),
        new PlanPair(instance.workflow().get("task02"), instance.hosts().get("HostB"))));
    var first = new SchedulePermutationSolution(1066, 2, null, plan, "makespan");
    var second = new SchedulePermutationSolution(1066, 2, null, plan, "makespan",
        SolutionOrigin.RANDOM);
    Field inheritedVariables = AbstractSolution.class.getDeclaredField("variables");
    inheritedVariables.setAccessible(true);

    for (var solution : List.of(first, second)) {
      assertTrue(((List<?>) inheritedVariables.get(solution)).isEmpty());
      assertSame(plan, solution.variables());
      assertEquals(2, solution.getLength());
      assertEquals(2, solution.objectives().length);
      assertEquals(0, solution.constraints().length);
    }
    assertThrows(IllegalArgumentException.class,
        () -> new SchedulePermutationSolution(-1, 2, null, plan, "makespan"));
    assertThrows(IllegalArgumentException.class,
        () -> new SchedulePermutationSolution(-1, 2, null, plan, "makespan", SolutionOrigin.RANDOM));
  }

  @Test
  void equalPlansHaveEqualHashesAfterCopyAndPlanReplacement() {
    var instance = loadFitnessTest();
    var firstPair = new PlanPair(instance.workflow().get("task01"), instance.hosts().get("HostA"));
    var secondPair = new PlanPair(instance.workflow().get("task02"), instance.hosts().get("HostB"));
    var plan = new ArrayList<>(List.of(firstPair, secondPair));
    var first = new SchedulePermutationSolution(1, 2, null, plan, "makespan");
    var second = new SchedulePermutationSolution(2, 2, null, new ArrayList<>(plan), "makespan");
    var copy = first.copy();

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertTrue(new HashSet<>(List.of(first)).contains(second));
    assertEquals(first, copy);
    assertEquals(first.hashCode(), copy.hashCode());
    assertNotSame(first.variables(), copy.variables());

    var replacement = List.of(secondPair);
    first.setPlan(replacement);
    second.setPlan(replacement);
    assertSame(replacement, first.variables());
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertEquals(plan, copy.variables());
  }

  @Test
  void crossoverAndMutationUseTheRealPlan() {
    var instance = loadFitnessTest();
    var firstPair = new PlanPair(instance.workflow().get("task01"), instance.hosts().get("HostA"));
    var secondPair = new PlanPair(instance.workflow().get("task02"), instance.hosts().get("HostB"));
    var firstPlan = List.of(firstPair, secondPair);
    var secondPlan = List.of(secondPair, firstPair);
    var first = new SchedulePermutationSolution(2, 2, null, firstPlan, "makespan");
    var second = new SchedulePermutationSolution(2, 2, null, secondPlan, "makespan");
    var operators = mock(Operators.class);
    when(operators.doCrossover(firstPlan, secondPlan)).thenReturn(secondPlan);
    when(operators.doCrossover(secondPlan, firstPlan)).thenReturn(firstPlan);
    when(operators.mutate(secondPlan)).thenReturn(firstPlan);

    var children = new ScheduleCrossover(1, operators).execute(List.of(first, second));
    assertEquals(secondPlan, children.get(0).variables());
    assertEquals(firstPlan, children.get(1).variables());
    var mutated = new ScheduleMutation(1, operators).execute(children.get(0));
    assertEquals(firstPlan, mutated.variables());
  }
}
