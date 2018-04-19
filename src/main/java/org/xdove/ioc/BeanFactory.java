package org.xdove.ioc;

import org.xdove.ioc.annotation.AutowiredHandle;
import org.xdove.ioc.annotation.BeanHandle;
import org.xdove.ioc.exception.NoSuchBeanException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.NoSuchFileException;
import java.util.*;

/**
 * bean factory.
 * @author Wszl
 * @since 0.1
 */
public class BeanFactory {

    private static final String CLASS_PATH_SPEC = "";
    private static final String FOLDER_SPEC = "\\";
    private static final String FOLDER_FIX_SPEC = "/";
    private static final String PACKAGE_SPLIT_SPEC = ".";
    private static final String CLASS_SUFFIX = ".class";

    private Map<String, Class> klassCache = new HashMap();
    private Map<Class, Object> instanceCache = new HashMap<>();

    private static BeanFactory factory;

    private BeanFactory() {}

    public static BeanFactory getInstance() {
        if (factory == null) {
            synchronized (BeanFactory.class) {
                if (factory == null) {
                    BeanFactory.factory = new BeanFactory();
                }
            }
        }
        return factory;
    }

    public Object getBean(Class klass) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchBeanException {
        Objects.requireNonNull(klass);
        Class beanKlass = klassCache.get(klass.getName());
        if (beanKlass == null) {
            throw new NoSuchBeanException(klass.getName());
        }
        Object bean = instanceCache.get(klass);
        if (bean == null) {
            bean = initBean(beanKlass);
        }
        return bean;
    }

    private boolean isBean(Class klass) {
        return BeanHandle.getInstance().handleClass(klass);
    }

    public void initBeanClass(Class klass) {
        Objects.requireNonNull(klass);
        klassCache.put(klass.getName(), klass);
    }

    private Object initBean(Class beanKlass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        var bean = beanKlass.getDeclaredConstructor().newInstance();
        inject(bean);
        instanceCache.put(beanKlass, bean);
        return bean;
    }

    public void inject(Object receive) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(receive);
        var handle = AutowiredHandle.getInstance();
        for (Field field : handle.handleClass(receive.getClass())) {
            var destInstance = instanceCache.get(field.getType());
            if (destInstance == null) {
                destInstance = initBean(field.getType());
            }
            var receiveInstance = instanceCache.get(receive.getClass());
            if (receiveInstance == null) {
                instanceCache.put(receive.getClass(), receive);
                receiveInstance = receive;
            }

            field.setAccessible(true);
            field.set(receiveInstance, destInstance);
        }
    }


    private String getClasspath() {
        String result = getClassLoader().getResource(CLASS_PATH_SPEC).getPath();
        if (result.contains(FOLDER_FIX_SPEC)) {
            result = result.replace(FOLDER_FIX_SPEC, FOLDER_SPEC);
        }
        if (result.startsWith(FOLDER_SPEC)) {
            result = result.substring(1);
        }
        return result;
    }

    private String parseBaseDirPath(String baseDir) {
        Objects.requireNonNull(baseDir);
        String basePath = baseDir.replace(PACKAGE_SPLIT_SPEC, FOLDER_SPEC);
        if (!basePath.endsWith(FOLDER_SPEC)) {
            basePath = basePath + FOLDER_SPEC;
        }
        return getClasspath() + basePath;
    }


    private List<File> getChildFolders(File root) {
        return Arrays.asList(root.listFiles(File::isDirectory));
    }

    private List<File> getChildFiles(File root) {
        return Arrays.asList(root.listFiles(File::isFile));
    }

    private String parseClasspath(File file, boolean suffix) throws IOException {
        final String classpath = getClasspath();
        final String filePath = file.getCanonicalPath();
        if (filePath.startsWith(classpath)) {
            String classPathFile = filePath.replace(classpath, "");
            if (suffix) {
                return classPathFile.substring(0, classPathFile.lastIndexOf(PACKAGE_SPLIT_SPEC)).replace(FOLDER_SPEC, PACKAGE_SPLIT_SPEC);
            } else {
                return classPathFile;
            }
        } else {
            return "";
        }
    }

    private List<File> filterClassFile(List<File> files) {
        Objects.requireNonNull(files);
        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            FilenameFilter filenameFilter = (dir, name) -> dir.getName().endsWith(name);
            if (!filenameFilter.accept(it.next(), CLASS_SUFFIX)) {
                it.remove();
            }
        }
        return files;
    }

    private void getAllClassFiles(final File baseDir, List<File> result) throws FileNotFoundException {
        Objects.requireNonNull(baseDir);
        if (!Objects.nonNull(result)) {
            result = new LinkedList<>();
        }
        if (!baseDir.exists()) {
            throw new FileNotFoundException();
        }
        result.addAll(getChildFiles(baseDir));
        for (File file : getChildFolders(baseDir)) {
            getAllClassFiles(file, result);
        }
    }


    private String getBaseDir() {
        return "";
    }

    private Class<?> loadClass(String name) throws ClassNotFoundException {
        var klass = getClassLoader().loadClass(name);
        return isBean(klass) ? klass : null;
    }

    private ClassLoader getClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    public void loadBaseDirClass() throws IOException, ClassNotFoundException {
        Objects.requireNonNull(getBaseDir());
        File baseDirFile = new File(parseBaseDirPath(getBaseDir()));
        if (!baseDirFile.exists()) {
            throw new NoSuchFileException(baseDirFile.getCanonicalPath());
        }

        List<File> result = new LinkedList<>();
        getAllClassFiles(baseDirFile, result);
        result = filterClassFile(result);
        for (File f : result) {
            String klassName = parseClasspath(f, true);
            Class klass = loadClass(klassName);
            if (klass == null) {
                continue;
            }
            klassCache.put(klassName, klass);
        }
    }

}
