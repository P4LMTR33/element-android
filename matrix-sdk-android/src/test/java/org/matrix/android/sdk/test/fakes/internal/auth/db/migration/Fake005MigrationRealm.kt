/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.test.fakes.internal.auth.db.migration

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import io.realm.DynamicRealm
import io.realm.DynamicRealmObject
import io.realm.RealmObjectSchema
import io.realm.RealmSchema
import org.matrix.android.sdk.internal.auth.db.SessionParamsEntityFields
import org.matrix.android.sdk.internal.auth.login.LoginType

class Fake005MigrationRealm {

    val instance: DynamicRealm = mockk()

    private val schema: RealmSchema = mockk()
    private val objectSchema: RealmObjectSchema = mockk()

    init {
        every { instance.schema } returns schema
        every { schema.get("SessionParamsEntity") } returns objectSchema
        every { objectSchema.addField(any(), any()) } returns objectSchema
        every { objectSchema.transform(any()) } returns objectSchema
    }

    fun verifyLoginTypeAdded() {
        transformFunctionSlot.clear()
        verifyLoginTypeFieldAddedAndTransformed()
        verifyTransformationSetsUnknownLoginType()
    }

    private fun verifyLoginTypeFieldAddedAndTransformed() {
        verifyOrder {
            objectSchema["SessionParamsEntity"]
            objectSchema.addField(SessionParamsEntityFields.LOGIN_TYPE, String::class.java)
            objectSchema.transform(capture(transformFunctionSlot))
        }
    }

    private fun verifyTransformationSetsUnknownLoginType() {
        val dynamicRealmObject: DynamicRealmObject = mockk()
        transformFunctionSlot.captured.invoke(dynamicRealmObject)
        verify { dynamicRealmObject.set(SessionParamsEntityFields.LOGIN_TYPE, LoginType.UNKNOWN.value) }
    }

    companion object {
        private val transformFunctionSlot = slot<(DynamicRealmObject) -> Unit>()
    }
}