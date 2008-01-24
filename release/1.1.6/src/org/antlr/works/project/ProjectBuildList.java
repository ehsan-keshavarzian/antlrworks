package org.antlr.works.project;

import org.antlr.xjlib.foundation.XJUtils;

import java.io.File;
import java.io.Serializable;
import java.util.*;
/*

[The "BSD licence"]
Copyright (c) 2005-2006 Jean Bovet
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

public class ProjectBuildList {

    protected Map<String, Map<Object, BuildFile>> files = new HashMap<String, Map<Object, BuildFile>>();

    public ProjectBuildList() {
    }

    public void addFile(String filePath, String type) {
        getMapForType(type).put(filePath, new BuildFile(filePath));
    }

    public void removeFile(String filePath, String type) {
        getMapForType(type).remove(filePath);
    }

    public void setFileDirty(ProjectFileItem item, boolean dirty) {
        setFileDirty(item.getFilePath(), item.getFileType(), dirty);
    }

    public void setFileDirty(String filePath, String type, boolean dirty) {
        BuildFile f = getMapForType(type).get(filePath);
        if(f != null)
            f.setDirty(dirty);
    }

    public void setIgnoreBuild(ProjectFileItem item, boolean ignore) {
        setIgnoreBuild(item.getFilePath(), item.getFileType(), ignore);
    }

    public void setIgnoreBuild(String filePath, String fileType, boolean ignore) {
        BuildFile f = getMapForType(fileType).get(filePath);
        if(f != null)
            f.setIgnore(ignore);
    }

    public boolean isIgnoreBuild(ProjectFileItem item) {
        return isIgnoreBuild(item.getFilePath(), item.getFileType());
    }

    public boolean isIgnoreBuild(String filePath, String fileType) {
        BuildFile f = getMapForType(fileType).get(filePath);
        if(f != null)
            return f.isIgnore();
        else
            return false;
    }

    public void setAllFilesToDirty(boolean flag) {
        for (Iterator<Map<Object, BuildFile>> typeIterator = files.values().iterator(); typeIterator.hasNext();) {
            Map<Object, BuildFile> m = typeIterator.next();
            for (Iterator<BuildFile> fileIterator = m.values().iterator(); fileIterator.hasNext();) {
                BuildFile file = fileIterator.next();
                file.setDirty(true);
            }
        }
    }

    public boolean isFileExisting(String filePath, String type) {
        return getMapForType(type).containsKey(filePath);
    }

    public BuildFile getBuildFile(ProjectFileItem item) {
        return getBuildFile(item.getFilePath(), item.getFileType());
    }

    public BuildFile getBuildFile(String filePath, String type) {
        for (Iterator<BuildFile> iterator = getBuildFilesOfType(type).iterator(); iterator.hasNext();) {
            BuildFile file = iterator.next();
            if(file.getFilePath().equals(filePath))
                return file;
        }
        return null;
    }

    public List<BuildFile> getBuildFilesOfType(String type) {
        List<BuildFile> files = new ArrayList<BuildFile>();
        for (Iterator<BuildFile> iterator = getMapForType(type).values().iterator(); iterator.hasNext();) {
            files.add(iterator.next());
        }
        return files;
    }

    public List<BuildFile> getDirtyBuildFilesOfType(String type) {
        List<BuildFile> dirtyFiles= new ArrayList<BuildFile>();
        for (Iterator<BuildFile> iterator = getBuildFilesOfType(type).iterator(); iterator.hasNext();) {
            BuildFile file = iterator.next();
            if(file.isDirty() && !file.isIgnore())
                dirtyFiles.add(file);
        }
        return dirtyFiles;
    }

    public boolean handleExternalModification(String filePath, String type) {
        BuildFile f = getBuildFile(filePath, type);
        if(f == null)
            return false;

        return f.handleExternalModification();
    }

    public void resetModificationDate(ProjectFileItem item) {
        BuildFile f = getBuildFile(item);
        if(f != null)
            f.resetModificationDate();
    }

    public boolean handleExternalModification() {
        boolean modified = false;
        for (Iterator<Map<Object, BuildFile>> typeIterator = files.values().iterator(); typeIterator.hasNext();) {
            Map<Object, BuildFile> m = typeIterator.next();
            for (Iterator<BuildFile> fileIterator = m.values().iterator(); fileIterator.hasNext();) {
                BuildFile file = fileIterator.next();
                if(file.handleExternalModification()) {
                    modified = true;
                }
            }
        }
        return modified;
    }

    public void setPersistentData(Map<String,Map> data) {
        files.clear();

        for (Iterator<String> typeIterator = data.keySet().iterator(); typeIterator.hasNext();) {
            String type = typeIterator.next();
            files.put(type, new HashMap<Object, BuildFile>());

            for (Iterator fileDataIterator = (data.get(type)).values().iterator(); fileDataIterator.hasNext();) {
                Map persistentData = (Map) fileDataIterator.next();
                BuildFile file = new BuildFile();
                file.setPersistentData(persistentData);
                (files.get(type)).put(file.filePath, file);
            }
        }
    }

    public Map<String,Map<Object,Map<Object,Serializable>>> getPersistentData() {
        Map<String,Map<Object,Map<Object,Serializable>>> data = new HashMap<String, Map<Object,Map<Object, Serializable>>>();

        for (Iterator<String> typeIterator = files.keySet().iterator(); typeIterator.hasNext();) {
            String type = typeIterator.next();
            Map<Object,Map<Object,Serializable>> dataFiles = new HashMap<Object, Map<Object,Serializable>>();
            for (Iterator<BuildFile> fileIterator = (files.get(type)).values().iterator(); fileIterator.hasNext();) {
                BuildFile file = fileIterator.next();
                dataFiles.put(file.filePath, file.getPersistentData());
            }
            data.put(type, dataFiles);
        }

        return data;
    }

    private Map<Object, BuildFile> getMapForType(String type) {
        Map<Object, BuildFile> m = files.get(type);
        if(m == null) {
            m = new HashMap<Object, BuildFile>();
            files.put(type, m);
        }
        return m;
    }

    public class BuildFile {

        private String filePath;
        private boolean dirty;
        private boolean ignore;
        private long modificationDate;

        public BuildFile() {
            this.dirty = true;
            this.ignore = false;
            resetModificationDate();
        }

        public BuildFile(String filePath) {
            this.filePath = filePath;
            this.dirty = true;
            this.ignore = false;
            resetModificationDate();
        }

        public void setDirty(boolean flag) {
            this.dirty = flag;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setIgnore(boolean ignore) {
            this.ignore = ignore;
        }

        public boolean isIgnore() {
            return ignore;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileFolder() {
            return XJUtils.getPathByDeletingLastComponent(getFilePath());
        }

        public boolean isModifiedOnDisk() {
            return modificationDate != getDateOfModificationOnDisk();
        }

        public void resetModificationDate() {
            modificationDate = getDateOfModificationOnDisk();
        }

        public boolean handleExternalModification() {
            if(isModifiedOnDisk()) {
                setDirty(true);
                resetModificationDate();
                return true;
            } else
                return false;
        }

        public long getDateOfModificationOnDisk() {
            File f = null;
            try {
                f = new File(filePath);
            } catch(Exception e) {
                // ignore excepton
            }

            if(f == null)
                return 0;
            else
                return f.lastModified();
        }

        static final String KEY_FILE_PATH = "KEY_FILE_PATH";
        static final String KEY_DIRTY = "KEY_DIRTY";
        static final String KEY_IGNORE = "KEY_IGNORE";
        static final String KEY_MODIFICATION_DATE = "KEY_MODIFICATION_DATE";

        public void setPersistentData(Map data) {
            filePath = (String) data.get(KEY_FILE_PATH);
            dirty = ((Boolean) data.get(KEY_DIRTY)).booleanValue();
            ignore = ((Boolean) data.get(KEY_IGNORE)).booleanValue();
            modificationDate = ((Long) data.get(KEY_MODIFICATION_DATE)).longValue();
        }

        public Map<Object,Serializable> getPersistentData() {
            Map<Object,Serializable> m = new HashMap<Object, Serializable>();
            m.put(KEY_FILE_PATH, filePath);
            m.put(KEY_DIRTY, Boolean.valueOf(dirty));
            m.put(KEY_IGNORE, Boolean.valueOf(ignore));
            m.put(KEY_MODIFICATION_DATE, new Long(modificationDate));
            return m;
        }

    }
}