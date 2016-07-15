/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.instrument.web.client;

import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.AsyncRestTemplate;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
		TraceWebAsyncClientAutoConfigurationTest.TestConfiguration.class }, webEnvironment = RANDOM_PORT)
public class TraceWebAsyncClientAutoConfigurationTest {

	@Autowired AsyncRestTemplate asyncRestTemplate;
	@Autowired MySyncClientHttpRequestFactory mySyncClientHttpRequestFactory;
	@Autowired MyAsyncClientHttpRequestFactory myAsyncClientHttpRequestFactory;

	@Test
	public void should_inject_to_async_rest_template_custom_client_http_request_factory() {
		then(this.asyncRestTemplate.getAsyncRequestFactory()).isInstanceOf(TraceAsyncClientHttpRequestFactoryWrapper.class);
		TraceAsyncClientHttpRequestFactoryWrapper wrapper = (TraceAsyncClientHttpRequestFactoryWrapper) this.asyncRestTemplate.getAsyncRequestFactory();
		then(wrapper.syncDelegate).isSameAs(this.mySyncClientHttpRequestFactory);
		then(wrapper.asyncDelegate).isSameAs(this.myAsyncClientHttpRequestFactory);
		then(this.asyncRestTemplate).isInstanceOf(TraceAsyncRestTemplate.class);
	}

	// tag::async_template_factories[]
	@EnableAutoConfiguration
	@Configuration
	public static class TestConfiguration {

		@Bean
		ClientHttpRequestFactory mySyncClientFactory() {
			return new MySyncClientHttpRequestFactory();
		}

		@Bean
		AsyncClientHttpRequestFactory myAsyncClientFactory() {
			return new MyAsyncClientHttpRequestFactory();
		}
	}
	// end::async_template_factories[]

	private static class MySyncClientHttpRequestFactory implements ClientHttpRequestFactory {
		@Override public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod)
				throws IOException {
			return null;
		}
	}
	private static class MyAsyncClientHttpRequestFactory implements AsyncClientHttpRequestFactory {
		@Override
		public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod)
				throws IOException {
			return null;
		}
	}

}