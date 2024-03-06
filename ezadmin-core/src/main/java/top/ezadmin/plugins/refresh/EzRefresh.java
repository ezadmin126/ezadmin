package top.ezadmin.plugins.refresh;

import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;

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
