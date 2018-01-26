package com.yezi.aidldemo.ser;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import com.yezi.aidldemo.ser.aidl.IStudentManager;
import com.yezi.aidldemo.ser.aidl.IStudentNoticeLisenner;
import com.yezi.aidldemo.ser.aidl.Student;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ZQ on 2017/10/14.
 */

public class StudentService extends Service {
    private String TAG = this.getClass().getSimpleName();
    private AtomicBoolean isServiceDead = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Student> students = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<IStudentNoticeLisenner> lisenners = new CopyOnWriteArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private Binder binder = new IStudentManager.Stub() {

        @Override
        public List<Student> getStudentList() throws RemoteException {
            return students;
        }

        @Override
        public void addStudent(Student stu) throws RemoteException {
            students.add(stu);
            for (IStudentNoticeLisenner lisenner:lisenners){
                lisenner.studentNotice(stu);
            }
        }

        @Override
        public void registerNoticeLisenner(IStudentNoticeLisenner lisenner) throws RemoteException {
            if (!lisenners.contains(lisenner)) {
                lisenners.add(lisenner);
            }else {
                Log.e(TAG,"The lisenner is existed!");
            }

        }

        @Override
        public void unregisterNoticeLisenner(IStudentNoticeLisenner lisenner) throws RemoteException {
            if (lisenners.contains(lisenner)) {
                lisenners.remove(lisenner);
            }else {
                Log.e(TAG,"The lisenner is not exist!");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        students.add(new Student("lucy",26));
        students.add(new Student("jone",23));
        new Thread(new Task()).start();
    }

    private void addNewStudent() throws RemoteException{
        Student stu = new Student( "stu" + students.size(),20);
        students.add(stu);
        Log.e(TAG,"The students size is !" + students.size());
        for (IStudentNoticeLisenner lisenner:lisenners){
            lisenner.studentNotice(stu);
        }
    }

    class Task implements  Runnable{
        @Override
        public void run() {
            while (!isServiceDead.get())  {
                try {
                    addNewStudent();
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDead.set(true);
    }
}
