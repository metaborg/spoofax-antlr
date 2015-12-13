package org.metaborg.antlr.meta;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileUtil;

class JarBuilder {
    /**
     * Jar the files from source to destination. Base will be stripped from the paths.
     * 
     * @param source
     * @param destination
     * @param base
     * @throws IOException
     * @throws FileSystemException
     */
    public void jar(FileObject source, FileObject destination, FileObject base) throws FileSystemException, IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try(JarOutputStream target = new JarOutputStream(destination.getContent().getOutputStream(), manifest)) {
            add(source, target, base);
        }
    }

    /**
     * Recursively add files from source to target. Strip given base from the path.
     * 
     * @param source
     * @param target
     * @throws IOException
     */
    protected void add(FileObject source, JarOutputStream target, FileObject base) throws IOException {
        if(source.getType() == FileType.FOLDER) {
            addDir(source, target, base);
        } else {
            addFile(source, target, base);
        }
    }

    /**
     * Add source file to target jar.
     * 
     * @param source
     * @param target
     * @throws IOException
     */
    protected void addFile(FileObject source, JarOutputStream target, FileObject base) throws IOException {
        String name = relativize(source, base);

        JarEntry jarEntry = new JarEntry(name);
        jarEntry.setTime(source.getContent().getLastModifiedTime());

        target.putNextEntry(jarEntry);

        FileUtil.writeContent(source, target);
    }

    /**
     * Add source folder and its children to target jar.
     * 
     * @param source
     * @param target
     * @throws IOException
     */
    protected void addDir(FileObject source, JarOutputStream target, FileObject base) throws IOException {
        String name = relativize(source, base);

        if(!name.isEmpty()) {
            JarEntry jarEntry = new JarEntry(StringUtils.appendIfMissing(name, "/"));
            jarEntry.setTime(source.getContent().getLastModifiedTime());

            target.putNextEntry(jarEntry);
            target.closeEntry();
        }

        for(FileObject file : source.getChildren()) {
            add(file, target, base);
        }
    }

    /**
     * Get the path to a file relative to the given base
     * 
     * @param source
     * @param base
     * @return
     * @throws FileSystemException
     */
    protected String relativize(FileObject file, FileObject base) throws FileSystemException {
        return base.getName().getRelativeName(file.getName());
    }
}