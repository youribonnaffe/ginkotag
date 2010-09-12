package com.appspot.ginkotag.model;

public interface IRabbitCommand {

	public IRabbitCommand say(String text);

	public RabbitCommandResponse execute();
}
