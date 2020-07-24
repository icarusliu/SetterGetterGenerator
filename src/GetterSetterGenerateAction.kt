import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiJavaFile

/**
 * Bean类Getter与Setter方法生成器
 * 为每个属性生成三个方法：常规的Setter与Getter，以及Builder方式的Setter
 *
 * @author  LiuQi 2019/6/24-9:16
 * @version V1.0
 */
class GetterSetterGenerateAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val project = anActionEvent.project ?: return
        val editor = anActionEvent.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) as? PsiJavaFile ?: return
        val classes = psiFile.classes
        if (classes.isEmpty()) {
            return
        }
        val elementFactory = PsiElementFactory.SERVICE.getInstance(project)
        classes.forEach {
            val fields = it.fields
            if (fields.isEmpty()) {
                return
            }
            for (field in fields) {
                val name = field.name
                val type = field.type
                if (field.modifierList?.hasExplicitModifier("static") == true) {
                    // 静态方法不进行处理
                    continue
                }

                // 针对每一个属性生成三个方法
                val builderSetter = elementFactory.createMethodFromText(createBuilderSetter(it.name!!, name, type.canonicalText), field)
                val normalSetter = elementFactory.createMethodFromText(createSetter(name, type.canonicalText), field)
                val getter = elementFactory.createMethodFromText(createGetter(name, type.canonicalText), field)

                // 需要使用WriteCommandAction进行，否则会报：Must not change PSI outside command or undo-transparent action
                WriteCommandAction.runWriteCommandAction(project) {
                    if (it.findMethodsByName(builderSetter.name).isEmpty()) {
                        it.add(builderSetter)
                    }
                    if (it.findMethodsByName(normalSetter.name).isEmpty()) {
                        it.add(normalSetter)
                    }
                    if (it.findMethodsByName(getter.name).isEmpty()) {
                        it.add(getter)
                    }
                }
            }
        }
    }

    private fun createBuilderSetter(className: String, name: String, type: String): String {
        return "public " +
                className +
                " " +
                name +
                "(" +
                type +
                " " +
                name +
                ") {" +
                "this." +
                name +
                " = " +
                name +
                ";" +
                "return this;}"
    }

    private fun createSetter(name: String, type: String): String {
        return "public void set" +
                name.substring(0, 1).toUpperCase() + name.substring(1) +
                "(" +
                type +
                " " +
                name +
                ") {" +
                "this." +
                name +
                " = " +
                name +
                ";}"
    }

    private fun createGetter(name: String, type: String): String {
        return "public " +
                type +
                " get" +
                name.substring(0, 1).toUpperCase() + name.substring(1) +
                "() {return this." +
                name +
                ";}"
    }
}