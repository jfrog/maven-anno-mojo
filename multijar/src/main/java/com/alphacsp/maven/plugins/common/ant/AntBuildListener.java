package com.alphacsp.maven.plugins.common.ant;

import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * Created by IntelliJ IDEA.
 * User: yoavl
 */
public class AntBuildListener implements BuildListener {
    private org.apache.maven.plugin.logging.Log log;
    private boolean removeEmpty = false;
    private boolean cutInPieces = false;

    public AntBuildListener(Log log) {
        this.log = log;
    }

    public void buildStarted(BuildEvent event) {
        logInfo(event);
    }

    public void buildFinished(BuildEvent event) {
        logInfo(event);
    }

    public void targetStarted(BuildEvent event) {
        logInfo(event);
    }

    public void targetFinished(BuildEvent event) {
        logInfo(event);
    }

    public void taskStarted(BuildEvent event) {
        logInfo(event);
    }

    public void taskFinished(BuildEvent event) {
        logInfo(event);
    }

    public void messageLogged(BuildEvent event) {
        String formattedMessage = event.getMessage().trim();
        if (removeEmpty && (formattedMessage.length() < 3)) {
            return;
        }
        int priority = event.getPriority();
        if (cutInPieces) {
            int firstCharPos = -1;
            int lastCharPos = -1;
            for (int i = 0; i < formattedMessage.length(); i++) {
                char ch = formattedMessage.charAt(i);
                if (ch > 32) {
                    if (lastCharPos == -1)
                        firstCharPos = i;
                    lastCharPos = i;
                }
                if (ch < 32 || (i - firstCharPos) >= 80) {
                    // New message
                    if ((lastCharPos - firstCharPos) > 3)
                        insertMessage(priority, formattedMessage.subSequence(
                                firstCharPos, lastCharPos + 1));
                    lastCharPos = -1;
                    firstCharPos = -1;
                }
            }
            if ((lastCharPos - firstCharPos) > 3)
                insertMessage(priority, formattedMessage.subSequence(
                        firstCharPos, lastCharPos + 1));
        } else {
            insertMessage(priority, formattedMessage);
        }
    }

    public boolean isRemoveEmpty() {
        return removeEmpty;
    }

    public void setRemoveEmpty(boolean removeEmpty) {
        this.removeEmpty = removeEmpty;
    }

    public boolean isCutInPieces() {
        return cutInPieces;
    }

    public void setCutInPieces(boolean cutInPieces) {
        this.cutInPieces = cutInPieces;
    }

    public Log getLog() {
        return log;
    }

    private void insertMessage(int priority, CharSequence formattedMessage) {
        if (priority > 3)
            log.debug(formattedMessage);
        else if (priority == 2)
            log.info(formattedMessage);
        else if (priority < 2)
            log.error(formattedMessage);
    }

    private void logInfo(BuildEvent event) {
        log.info(event.getMessage());
    }

}
