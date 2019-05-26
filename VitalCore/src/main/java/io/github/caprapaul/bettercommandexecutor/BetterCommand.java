package io.github.caprapaul.bettercommandexecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BetterCommand
{
    String name();
    int target() default (CommandTarget.PLAYER | CommandTarget.CONSOLE | CommandTarget.COMMAND_BLOCK);
}

