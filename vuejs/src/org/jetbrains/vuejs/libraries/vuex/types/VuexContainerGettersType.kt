// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.libraries.vuex.types

import com.intellij.lang.javascript.psi.JSRecordType
import com.intellij.lang.javascript.psi.JSType
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement
import com.intellij.lang.javascript.psi.types.JSRecordTypeImpl
import com.intellij.lang.javascript.psi.types.JSSimpleRecordTypeImpl
import com.intellij.lang.javascript.psi.types.JSTypeSource
import com.intellij.psi.PsiElement
import org.jetbrains.vuejs.libraries.vuex.model.store.VuexContainer
import org.jetbrains.vuejs.libraries.vuex.model.store.VuexStoreContext
import org.jetbrains.vuejs.libraries.vuex.model.store.VuexStoreNamespace
import org.jetbrains.vuejs.model.VueImplicitElement

class VuexContainerGettersType private constructor(source: JSTypeSource, element: PsiElement, baseNamespace: VuexStoreNamespace)
  : VuexContainerPropertyTypeBase(source, element, baseNamespace) {

  constructor(element: PsiElement, baseNamespace: VuexStoreNamespace)
    : this(JSTypeSource(element.containingFile, element, JSTypeSource.SourceLanguage.TS, true), element, baseNamespace)

  override val kind: String = "getters"

  override fun copyWithNewSource(source: JSTypeSource): JSType {
    return VuexContainerGettersType(source, element, baseNamespace)
  }

  override fun createStateRecord(context: VuexStoreContext, baseNamespace: String): JSRecordType? {
    val result = mutableListOf<JSRecordType.TypeMember>()
    context.visitSymbols(VuexContainer::getters) { fullName, symbol ->
      if (fullName.startsWith(baseNamespace)) {
        result.add(JSRecordTypeImpl.PropertySignatureImpl(
          fullName.substring(baseNamespace.length), symbol.jsType, false, false,
          VueImplicitElement(fullName.substring(baseNamespace.length), symbol.jsType, symbol.source,
                             JSImplicitElement.Type.Property)))
      }
    }
    return JSSimpleRecordTypeImpl(source, result)
  }
}
