import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *  
 *
 * @author  LiuQi 2019/6/24-9:16
 * @version V1.0
 **/
public class GetterSetterGenerateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (null == project || null == editor) {
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile)psiFile;
        PsiClass[] classes = javaFile.getClasses();
        if (0 == classes.length) {
            return;
        }

        PsiElementFactory elementFactory = PsiElementFactory.SERVICE.getInstance(project);

        for (PsiClass aClass : classes) {
            PsiField[] fields = aClass.getFields();

            if (0 == fields.length) {
                continue;
            }

            for (PsiField allField : fields) {
                String name = allField.getName();
                PsiType type = allField.getType();
                if (Objects.requireNonNull(allField.getModifierList()).hasExplicitModifier("static")) {
                    // 静态方法不进行处理
                    continue;
                }

                // 针对每一个属性生成三个方法
                PsiMethod builderSetter = elementFactory.createMethodFromText(createBuilderSetter(aClass.getName(), name, type.getCanonicalText()), allField);
                PsiMethod normalSetter = elementFactory.createMethodFromText(createSetter(name, type.getCanonicalText()), allField);
                PsiMethod getter = elementFactory.createMethodFromText(createGetter(name, type.getCanonicalText()), allField);

                // 需要使用WriteCommandAction进行，否则会报：Must not change PSI outside command or undo-transparent action
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    if (0 == aClass.findMethodsByName(builderSetter.getName()).length) {
                        aClass.add(builderSetter);
                    }

                    if (0 == aClass.findMethodsByName(normalSetter.getName()).length) {
                        aClass.add(normalSetter);
                    }

                    if (0 == aClass.findMethodsByName(getter.getName()).length) {
                        aClass.add(getter);
                    }
                });
            }
        }
    }

    private String createBuilderSetter(String className, String name, String type) {
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
                "return this;}";
    }

    private String createSetter(@NotNull String name, String type) {
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
                ";}";
    }

    private String createGetter(String name, String type) {
        return "public " +
                type +
                " get" +
                name.substring(0, 1).toUpperCase() + name.substring(1) +
                "() {return this." +
                name +
                ";}";
    }

}
