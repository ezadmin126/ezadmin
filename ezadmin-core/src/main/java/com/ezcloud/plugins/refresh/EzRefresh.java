package com.ezcloud.plugins.refresh;

import com.ezcloud.dao.FormDao;
import com.ezcloud.dao.ListDao;

import java.io.File;
import java.io.IOException;

public abstract class EzRefresh {
      protected   void refreshList(File file) throws Exception {
            ListDao.getInstance().loadListFile(file);
      }
      protected void refreshForm(File file) throws Exception {
            FormDao.getInstance().loadFormFile(file);
      }
      protected  void refreshDetail(File file){

      }
      public  abstract void refreshAll() throws IOException;
}
