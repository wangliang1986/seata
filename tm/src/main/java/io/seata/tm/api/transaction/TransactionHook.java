/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.tm.api.transaction;

import io.seata.tm.api.GlobalTransaction;

/**
 * @author guoyao
 */
public interface TransactionHook {

    /**
     * before tx begin
     */
    void beforeBegin(GlobalTransaction tx);

    /**
     * after tx begin
     */
    void afterBegin(GlobalTransaction tx);

    /**
     * before tx commit
     */
    void beforeCommit(GlobalTransaction tx);

    /**
     * after tx commit
     */
    void afterCommit(GlobalTransaction tx);

    /**
     * before tx rollback
     */
    void beforeRollback(GlobalTransaction tx);

    /**
     * after tx rollback
     */
    void afterRollback(GlobalTransaction tx);

    /**
     * after tx all Completed
     */
    void afterCompletion(GlobalTransaction tx);
}
