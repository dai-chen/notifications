/*
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  The OpenSearch Contributors require contributions made to
 *  this file be licensed under the Apache-2.0 license or a
 *  compatible open source license.
 *
 *  Modifications Copyright OpenSearch Contributors. See
 *  GitHub history for details.
 */
package org.opensearch.notifications.spi.factory

import org.junit.Test
import org.opensearch.notifications.spi.model.destination.DestinationType
import kotlin.test.assertNotNull

internal class DestinationFactoryProviderTests {

    @Test
    fun `Get factory for SNS destination should return SNS factory`() {
        assertNotNull(DestinationFactoryProvider.getFactory(DestinationType.SNS))
    }
}