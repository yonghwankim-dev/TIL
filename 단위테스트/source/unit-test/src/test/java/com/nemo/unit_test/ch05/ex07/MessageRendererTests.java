package com.nemo.unit_test.ch05.ex07;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MessageRendererTests {
	interface Renderer{
		String render(Message message);
	}

	static class MessageRenderer implements Renderer{
		public final List<Renderer> subRenderers;

		public MessageRenderer() {
			this.subRenderers = List.of(
				new HeaderRenderer(),
				new BodyRenderer(),
				new FooterRenderer()
			);
		}

		@Override
		public String render(Message message) {
			return subRenderers.stream()
				.map(r->r.render(message))
				.collect(Collectors.joining());
		}
	}

	static class HeaderRenderer implements Renderer{
		@Override
		public String render(Message message) {
			return String.format("<head>%s</head>", message.getHeader());
		}
	}

	static class BodyRenderer implements Renderer{
		@Override
		public String render(Message message) {
			return String.format("<body>%s</body>", message.getBody());
		}
	}

	static class FooterRenderer implements Renderer{
		@Override
		public String render(Message message) {
			return String.format("<footer>%s</footer>", message.getFooter());
		}
	}

	static class Message{
		private final String header;
		private final String body;
		private final String footer;

		public Message(String header, String body, String footer) {
			this.header = header;
			this.body = body;
			this.footer = footer;
		}

		public String getHeader() {
			return header;
		}

		public String getBody() {
			return body;
		}

		public String getFooter() {
			return footer;
		}
	}

	@Test
	void render_message(){
		// given
		MessageRenderer renderer = new MessageRenderer();
		// when
		List<Renderer> subRenderers = renderer.subRenderers;
		// then
		// 가장 안좋은 검증 : 내부 리스트의 순서와 구체 클래스 타입까지 검증
		Assertions.assertThat(subRenderers)
			.hasSize(3)
			.hasExactlyElementsOfTypes(
				HeaderRenderer.class,
				BodyRenderer.class,
				FooterRenderer.class
			);
	}
}
