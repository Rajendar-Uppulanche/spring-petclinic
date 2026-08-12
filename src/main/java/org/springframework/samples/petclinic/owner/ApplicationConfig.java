/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Dave Syer
 */
@Configuration
@ComponentScan(basePackages = {"org.springframework.samples.petclinic"}, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "org.springframework.samples.petclinic.util.*")
})
@EntityScan(basePackages = "org.springframework.samples.petclinic.owner")
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

}
