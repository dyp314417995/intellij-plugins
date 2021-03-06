// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlDocument
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.util.HtmlUtil
import com.intellij.xml.util.HtmlUtil.TEMPLATE_TAG_NAME
import org.jetbrains.vuejs.VueBundle
import org.jetbrains.vuejs.lang.html.VueLanguage

class DuplicateTagInspection : LocalInspectionTool() {
  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
    return object : XmlElementVisitor() {
      override fun visitXmlTag(tag: XmlTag?) {
        if (tag?.language != VueLanguage.INSTANCE) return
        if (TEMPLATE_TAG_NAME != tag.name && !HtmlUtil.isScriptTag(tag)) return
        val parent = tag.parent as? XmlDocument ?: return
        if (PsiTreeUtil.getChildrenOfType(parent, XmlTag::class.java).any { it != tag && it.name == tag.name }) {
          holder.registerProblem(tag, VueBundle.message("vue.inspection.message.duplicate.tag", tag.name), DeleteTagFix(tag))
        }
      }
    }
  }
}

class DeleteTagFix(tag: XmlTag, private val tagName: String = tag.name) : LocalQuickFixOnPsiElement(tag) {
  override fun getFamilyName(): String = VueBundle.message("vue.quickfix.delete.tag.family")
  override fun getText(): String = VueBundle.message("vue.quickfix.delete.tag.text", tagName)

  override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
    startElement.delete()
  }
}
