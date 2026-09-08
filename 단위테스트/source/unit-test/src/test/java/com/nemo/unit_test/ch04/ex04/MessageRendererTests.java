package com.nemo.unit_test.ch04.ex04;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageRendererTests {

	static class Message{
		private final String header;
		private final String body;
		private final String footer;

		public Message(String header, String body, String footer) {
			this.header = header;
			this.body = body;
			this.footer = footer;
		}
	}

	interface IRenderer{
		String render(Message message);
	}

	static class MessageRenderer implements IRenderer{

		private final List<IRenderer> subRenderers;

		public MessageRenderer(List<IRenderer> subRenderers) {
			this.subRenderers = subRenderers;
		}

		@Override
		public String render(Message message) {
			return subRenderers.stream()
				.map(x->x.render(message))
				.collect(Collectors.joining(""));
		}

	}

	static class HeaderRenderer implements IRenderer{


		@Override
		public String render(Message message) {
			return String.format("<head>%s</head>", message.header);
		}
	}

	static class BodyRenderer implements IRenderer{


		@Override
		public String render(Message message) {
			return String.format("<body>%s</body>", message.body);
		}
	}

	static class FooterRenderer implements IRenderer{


		@Override
		public String render(Message message) {
			return String.format("<footer>%s</footer>", message.footer);
		}
	}


	// good case
	@Test
	void rendering_a_message(){
		// given
		List<IRenderer> subRenderers = List.of(
			new HeaderRenderer(),
			new BodyRenderer(),
			new FooterRenderer()
		);
		IRenderer sut = new MessageRenderer(subRenderers);
		Message message = new Message("h", "b", "f");
		// when
		String html = sut.render(message);
		// then
		String expected = "<head>h</head><body>b</body><footer>f</footer>";
		Assertions.assertThat(html).isEqualTo(expected);
	}
}
