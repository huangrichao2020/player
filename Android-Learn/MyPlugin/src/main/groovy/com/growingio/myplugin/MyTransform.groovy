package com.growingio.myplugin;

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

/**
 * 向calss文件中注入代码
 */
class MyTransform extends Transform {

    private static final String DEFAULT_NAME = "__MyTransformEditClasses__"

    private static final Set<QualifiedContent.Scope> SCOPES = new HashSet<>();

    static {
        SCOPES.add(QualifiedContent.Scope.PROJECT);
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS);
        SCOPES.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES);
    }

    private Project project

    def injectCode = "android.widget.Toast.makeText(this,\"这是我插入xxxx代码\",android.widget.Toast.LENGTH_SHORT).show();"

    MyTransform(Project project) {
        this.project = project
    }

    /**
     * 设置自定义的Transform对应的Task名称
     * @return Task名称
     */
    @Override
    public String getName() {
        return DEFAULT_NAME
    }

    /**
     * 需要处理的数据类型，CONTENT_CLASS代表处理class文件
     * @return
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指定Transform的作用范围
     * @return
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return SCOPES
    }

    /**
     * 指明当前Transform是否支持增量编译
     * @return
     */
    @Override
    public boolean isIncremental() {
        return false
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        TransformOutputProvider outputProvider = transformInvocation.outputProvider;

        for (TransformInput input : transformInvocation.inputs) {

            if (null == input) continue
            //遍历文件夹
            for (DirectoryInput directoryInput : input.directoryInputs) {
                // 注入代码
                InjectClass.inject(directoryInput.file.absolutePath, project, injectCode)
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //遍历jar文件 对jar不操作，但是要输出到out路径
            for (JarInput jarInput : input.jarInputs) {
                // 重命名输出文件
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

}