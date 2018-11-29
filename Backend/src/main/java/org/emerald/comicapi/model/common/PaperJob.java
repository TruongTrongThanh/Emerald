package org.emerald.comicapi.model.common;

import org.emerald.comicapi.model.data.Paper;

public class PaperJob implements Runnable {
    private Paper paper;
    public PaperJob(Paper paper) {
        this.paper = paper;
    }
    @Override
    public void run() {
        System.out.print(paper);
    }
}
