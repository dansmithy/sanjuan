package com.github.dansmithy.sanjuan.game.aspect;

import javax.inject.Inject;
import javax.inject.Named;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.github.dansmithy.sanjuan.game.CalculationService;
import com.github.dansmithy.sanjuan.model.Game;

@Named
@Aspect
public class GameProcessorAspect {

	private final CalculationService calculationService;

	@Inject
	public GameProcessorAspect(CalculationService calculationService) {
		this.calculationService = calculationService;
	}

	@Pointcut("@annotation(com.github.dansmithy.sanjuan.game.aspect.ProcessGame)")
	public void methodAnnotatedWithProcessGame() {}

	@AfterReturning(pointcut = "com.github.dansmithy.sanjuan.game.aspect.GameProcessorAspect.methodAnnotatedWithProcessGame()", returning = "gameObject")
	public void doGameAug(Object gameObject) {

		Game game = (Game) gameObject;
		calculationService.processPlayers(game.getPlayers());
	}

}
