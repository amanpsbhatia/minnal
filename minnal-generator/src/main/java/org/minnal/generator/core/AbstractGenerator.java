/**
 *
 */
package org.minnal.generator.core;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.javalite.common.Inflector;
import org.minnal.generator.exception.MinnalGeneratorException;
import org.minnal.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 */
public abstract class AbstractGenerator implements Generator {

    private List<Generator> generators = new ArrayList<Generator>();

    protected File baseDir;

    protected String applicationName;

    private static final Logger logger = LoggerFactory.getLogger(AbstractGenerator.class);

    public static final String MAIN_FOLDER = "src/main";

    public static final String TEST_FOLDER = "src/test";

    public static final String MAIN_JAVA_FOLDER = MAIN_FOLDER + "/java";

    public static final String TEST_JAVA_FOLDER = TEST_FOLDER + "/java";

    public static final String MAIN_RESOURCES_FOLDER = MAIN_FOLDER + "/resources";

    public static final String TEST_RESOURCES_FOLDER = TEST_FOLDER + "/resources";

    public static final String MAIN_META_INF_FOLDER = MAIN_RESOURCES_FOLDER + "/META-INF";

    public static final String TEST_META_INF_FOLDER = TEST_RESOURCES_FOLDER + "/META-INF";

    public static final String MAIN_SERVICES_FOLDER = MAIN_META_INF_FOLDER + "/services";

    public static final String TEST_SERVICES_FOLDER = TEST_META_INF_FOLDER + "/services";

    public AbstractGenerator(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void init() {
        applicationName = Inflector.camelize(baseDir.getName().toLowerCase().replace('-', '_'));
    }

    protected String getBasePackage() {
        return "com." + Inflector.underscore(applicationName).replace('_', '.');
    }

    protected String getDomainPackage() {
        return getBasePackage() + "." + "domain";
    }

    protected String getResourcesPackage() {
        return getBasePackage() + "." + "resources";
    }

    protected String getApplicationClassName() {
        return getBasePackage() + "." + applicationName + "Application";
    }

    protected String getApplicationConfigClassName() {
        return getBasePackage() + "." + applicationName + "Configuration";
    }

    protected File getResourcesFolder(boolean checkExists) {
        return getFile(baseDir, MAIN_RESOURCES_FOLDER, checkExists);
    }

    protected File getJavaFolder(boolean checkExists) {
        return getFile(baseDir, MAIN_JAVA_FOLDER, checkExists);
    }

    protected File getMetaInfFolder(boolean checkExists) {
        return getFile(baseDir, MAIN_META_INF_FOLDER, checkExists);
    }

    protected File getServicesFolder(boolean checkExists) {
        return getFile(baseDir, MAIN_SERVICES_FOLDER, checkExists);
    }

    protected File getFile(File baseDir, String name, boolean checkExists) {
        File file = new File(baseDir, name);
        if (checkExists && !file.exists()) {
            throw new MinnalGeneratorException("File " + file.getAbsolutePath() + " doesn't exist");
        }
        return file;
    }

    protected File createPackage(String packageName) {
        return createPackage(packageName, MAIN_JAVA_FOLDER);
    }

    protected File createPackage(String packageName, String sourceFolder) {
        File srcDir = getFile(baseDir, sourceFolder, true);
        File pkg = new File(srcDir, packageName.replace('.', '/'));
        if (!pkg.exists()) {
            if (!pkg.mkdirs()) {
                throw new IllegalStateException("Failed while creating the package - " + pkg.getPath());
            }
        }
        return pkg;
    }

    protected void addGenerator(Generator generator) {
        generators.add(generator);
    }

    protected File createFolder(String folderName) {
        return createFolder(baseDir, folderName);
    }

    protected File createFolder(File basedir, String folderName) {
        logger.info("Creating the folder {} under {}", folderName, basedir);
        File folder = new File(basedir, folderName);
        if (!folder.mkdirs()) {
            throw new IllegalStateException("Failed while creating the folder - " + folder.getPath());
        }
        return folder;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Generator> T generatorFor(Class<T> generatorClass) {
        for (Generator generator : generators) {
            if (generator.getClass().equals(generatorClass)) {
                return (T) generator;
            }
        }
        return null;
    }

    @Override
    public void generate() {
        for (Generator generator : generators) {
            generator.init();
            generator.generate();
        }
    }

    protected void serializeTo(File file, Object content, Serializer serializer) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            serializer.serialize(content, stream);
        } catch (Exception e) {
            throw new MinnalGeneratorException("Failed while writing the file " + file.getAbsolutePath(), e);
        } finally {
            if (stream != null) {
                closeStream(stream);
            }
        }
    }

    protected <T> T deserializeFrom(File file, Serializer serializer, Class<T> clazz) {
        if (!file.exists()) {
            return null;
        }

        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return serializer.deserialize(stream, clazz);
        } catch (Exception e) {
            throw new MinnalGeneratorException("Failed while creating the file " + file.getAbsolutePath());
        } finally {
            closeStream(stream);
        }
    }

    protected void closeStream(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
        }
    }
}
