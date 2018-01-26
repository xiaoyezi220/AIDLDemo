package com.yezi.aidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yezi.aidldemo.ser.StudentService;
import com.yezi.aidldemo.ser.aidl.IStudentManager;
import com.yezi.aidldemo.ser.aidl.IStudentNoticeLisenner;
import com.yezi.aidldemo.ser.aidl.Student;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IStudentManager iStudentManager;
    private String TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, StudentService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iStudentManager = IStudentManager.Stub.asInterface(iBinder);

            try {
                iStudentManager.asBinder().linkToDeath(deathRecipient,0);

                /***
                 *注册通知新学生的接口
                 */
                iStudentManager.registerNoticeLisenner(studentNoticeLisenner);
                iStudentManager.addStudent(new Student("qq",18));
                List<Student> students = iStudentManager.getStudentList();
                for (Student student : students){
                    Log.e(TAG,"name == " + student.getName());
                    Log.e(TAG,"age == " + student.getAge());
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    //添加新学生的通知接口
    private IStudentNoticeLisenner studentNoticeLisenner  = new IStudentNoticeLisenner.Stub() {
        @Override
        public void studentNotice(Student stu) throws RemoteException {
            Log.e(TAG,"new stu name == " + stu.getName());
            Log.e(TAG,"new stu age == " + stu.getAge());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(iStudentManager != null && iStudentManager.asBinder().isBinderAlive()){
            try {
                iStudentManager.unregisterNoticeLisenner(studentNoticeLisenner);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
    }

    /***
     * 远程服务死亡回调接口
     */
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if(iStudentManager == null){
                return;
            }
            Log.e(TAG,"The service is dead!");
            iStudentManager.asBinder().unlinkToDeath(deathRecipient,0);
            iStudentManager =null;
        }
    };
}
