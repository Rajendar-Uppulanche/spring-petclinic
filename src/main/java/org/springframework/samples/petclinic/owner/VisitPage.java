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

import java.util.List;

/**
 * A simple wrapper for a page of visits.
 *
 * @author Vitaliy Fedoriv
 */
public class VisitPage {

	private final List<Visit> content;

	private final long totalElements;

	private final int currentPage;

	private final int pageSize;

	public VisitPage(List<Visit> content, long totalElements, int currentPage, int pageSize) {
		this.content = content;
		this.totalElements = totalElements;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
	}

	public List<Visit> getContent() {
		return content;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalPages() {
		return (int) Math.ceil((double) totalElements / pageSize);
	}

}
