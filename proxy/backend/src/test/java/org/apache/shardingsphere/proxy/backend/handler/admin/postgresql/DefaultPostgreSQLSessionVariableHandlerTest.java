/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.backend.handler.admin.postgresql;

import org.apache.shardingsphere.infra.util.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.proxy.backend.handler.admin.executor.ReplayRequiredSessionVariables;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.backend.session.RequiredSessionVariableRecorder;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public final class DefaultPostgreSQLSessionVariableHandlerTest {
    
    @Test
    public void assertHandleDiscard() {
        ConnectionSession connectionSession = mock(ConnectionSession.class);
        new DefaultPostgreSQLSessionVariableHandler().handle(connectionSession, "", "");
        verifyNoInteractions(connectionSession);
    }
    
    @Test
    public void assertHandleRecord() {
        ConnectionSession connectionSession = mock(ConnectionSession.class);
        when(connectionSession.getRequiredSessionVariableRecorder()).thenReturn(mock(RequiredSessionVariableRecorder.class));
        try (MockedStatic<TypedSPILoader> typedSPILoader = mockStatic(TypedSPILoader.class)) {
            ReplayRequiredSessionVariables replayRequiredSessionVariables = mock(ReplayRequiredSessionVariables.class);
            when(replayRequiredSessionVariables.getReplayRequiredVariables()).thenReturn(Collections.singleton("datestyle"));
            typedSPILoader.when(() -> TypedSPILoader.findService(ReplayRequiredSessionVariables.class, "PostgreSQL")).thenReturn(Optional.of(replayRequiredSessionVariables));
            new DefaultPostgreSQLSessionVariableHandler().handle(connectionSession, "datestyle", "postgres");
            verify(connectionSession.getRequiredSessionVariableRecorder()).setVariable("datestyle", "postgres");
        }
    }
}
