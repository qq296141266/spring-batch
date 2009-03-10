/*
 * Copyright 2006-2007 the original author or authors.
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

package org.springframework.batch.core.exlore.support;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collections;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;

/**
 * Test {@link SimpleJobExplorer}. 
 * 
 * @author Dave Syer
 * 
 */
public class SimpleJobExplorerTests extends TestCase {

	SimpleJobExplorer jobExplorer;

	JobExecutionDao jobExecutionDao;
	
	JobInstanceDao jobInstanceDao;
	
	StepExecutionDao stepExecutionDao;

	JobInstance jobInstance = new JobInstance(111L, new JobParameters(), "job");
	
	JobExecution jobExecution = new JobExecution(jobInstance, 123L);

	public void setUp() throws Exception {

		jobExecutionDao = createMock(JobExecutionDao.class);
		jobInstanceDao = createMock(JobInstanceDao.class);
		stepExecutionDao = createMock(StepExecutionDao.class);

		jobExplorer = new SimpleJobExplorer(jobInstanceDao, jobExecutionDao, stepExecutionDao);

	}

	@Test
	public void testGetJobExecution() throws Exception {
		expect(jobExecutionDao.getJobExecution(123L)).andReturn(jobExecution);
		expect(jobInstanceDao.getJobInstance(jobExecution)).andReturn(jobInstance);
		stepExecutionDao.addStepExecutions(jobExecution);
		expectLastCall();
		replay(jobExecutionDao, jobInstanceDao, stepExecutionDao);
		jobExplorer.getJobExecution(123L);
		verify(jobExecutionDao, jobInstanceDao, stepExecutionDao);
	}

	@Test
	public void testMissingGetJobExecution() throws Exception {
		expect(jobExecutionDao.getJobExecution(123L)).andReturn(null);
		replay(jobExecutionDao);
		assertNull(jobExplorer.getJobExecution(123L));
		verify(jobExecutionDao);
	}

	@Test
	public void testGetStepExecution() throws Exception {
		expect(jobExecutionDao.getJobExecution(123L)).andReturn(jobExecution);
		expect(stepExecutionDao.getStepExecution(jobExecution, 123L)).andReturn(null);
		stepExecutionDao.addStepExecutions(jobExecution);
		expectLastCall();
		replay(jobExecutionDao, stepExecutionDao);
		jobExplorer.getStepExecution(jobExecution.getId(), 123L);
		verify(jobExecutionDao, stepExecutionDao);
	}

	@Test
	public void testFindRunningJobExecutions() throws Exception {
		expect(jobExecutionDao.findRunningJobExecutions("job")).andReturn(Collections.singleton(jobExecution));
		expect(jobInstanceDao.getJobInstance(jobExecution)).andReturn(jobInstance);
		stepExecutionDao.addStepExecutions(jobExecution);
		expectLastCall();
		replay(jobExecutionDao, jobInstanceDao, stepExecutionDao);
		jobExplorer.findRunningJobExecutions("job");
		verify(jobExecutionDao, jobInstanceDao, stepExecutionDao);
	}

	@Test
	public void testFindJobExecutions() throws Exception {
		expect(jobExecutionDao.findJobExecutions(jobInstance)).andReturn(Collections.singletonList(jobExecution));
		expect(jobInstanceDao.getJobInstance(jobExecution)).andReturn(jobInstance);
		stepExecutionDao.addStepExecutions(jobExecution);
		expectLastCall();
		replay(jobExecutionDao, jobInstanceDao, stepExecutionDao);
		jobExplorer.getJobExecutions(jobInstance);
		verify(jobExecutionDao, jobInstanceDao, stepExecutionDao);
	}

	@Test
	public void testGetJobInstance() throws Exception {
		jobInstanceDao.getJobInstance(111L);
		EasyMock.expectLastCall().andReturn(jobInstance);
		replay(jobExecutionDao, jobInstanceDao, stepExecutionDao);
		jobExplorer.getJobInstance(111L);
		verify(jobExecutionDao, jobInstanceDao, stepExecutionDao);
	}

	@Test
	public void testGetLastJobInstances() throws Exception {
		jobInstanceDao.getJobInstances("foo", 0, 1);
		EasyMock.expectLastCall().andReturn(Collections.singletonList(jobInstance));
		replay(jobExecutionDao, jobInstanceDao, stepExecutionDao);
		jobExplorer.getJobInstances("foo", 0, 1);
		verify(jobExecutionDao, jobInstanceDao, stepExecutionDao);
	}

}