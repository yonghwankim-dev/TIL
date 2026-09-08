package com.nemo.unit_test.ch04.ex01;

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

		public List<IRenderer> getSubRenderers() {
			return subRenderers;
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


	// bad case
	@Test
	void messageRenderer_uses__correct_sub_renders(){
		// given
		IRenderer headerRenderer = new HeaderRenderer();
		IRenderer bodyRenderer = new BodyRenderer();
		IRenderer footerRenderer = new FooterRenderer();
		List<IRenderer> subRenders = List.of(
			headerRenderer,
			bodyRenderer,
			footerRenderer
		);
		MessageRenderer sut = new MessageRenderer(subRenders);
		// when
		List<IRenderer> renderers = sut.getSubRenderers();
		// then
		// 최종 결과는 동일하나 내부 호출순서만 변경되어도 테스트는 실패하게 되어있음. (거짓 양성)
		Assertions.assertThat(renderers)
			.hasSize(3)
			.containsExactly(headerRenderer, bodyRenderer, footerRenderer);
	}
}
