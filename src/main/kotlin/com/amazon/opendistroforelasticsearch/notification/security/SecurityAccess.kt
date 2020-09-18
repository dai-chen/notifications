/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package com.amazon.opendistroforelasticsearch.notification.security

import org.elasticsearch.SpecialPermission
import java.io.IOException
import java.security.AccessController
import java.security.PrivilegedActionException
import java.security.PrivilegedExceptionAction

/**
 * Ref:
 * https://www.elastic.co/guide/en/elasticsearch/plugins/current/plugin-authors.html#_java_security_permissions
 */
object SecurityAccess {
    /**
     * Execute the operation in privileged mode.
     */
    @Throws(IOException::class)
    fun <T> doPrivileged(operation: PrivilegedExceptionAction<T>?): T {
        SpecialPermission.check()
        return try {
            AccessController.doPrivileged(operation)
        } catch (e: PrivilegedActionException) {
            throw (e.cause as IOException?)!!
        }
    }
}