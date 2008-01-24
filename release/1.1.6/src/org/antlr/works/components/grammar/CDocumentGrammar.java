/*

[The "BSD licence"]
Copyright (c) 2005 Jean Bovet
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

package org.antlr.works.components.grammar;

import org.antlr.works.components.ComponentContainer;
import org.antlr.works.components.ComponentDocument;
import org.antlr.works.prefs.AWPrefs;

import java.io.File;

public class CDocumentGrammar extends ComponentDocument {

    public boolean performSave(boolean saveAs) {
        // Make sure the document can be saved (SCM opened, etc)
        // before calling the super class method to do
        // the actual job
        ComponentContainer w = getContainer();
        if(w.willSaveDocument()) {
            if(documentPath != null && !saveAs && AWPrefs.getBackupFileEnabled()) {
                // Create the backup file if needed
                File backup = new File(documentPath+"~");
                if(backup.exists()) backup.delete();
                new File(documentPath).renameTo(backup);
            }
            return super.performSave(saveAs);
        } else {
            return false;
        }
    }

}