// IStudentManager.aidl
package com.yezi.aidldemo.ser.aidl;

// 这里引用的是aidl包里parcelable的对象，另外这个parcelable的Student对象和普通Student对象同名
// 包名也要相同
import com.yezi.aidldemo.ser.aidl.Student;

//即使连个aidl文件位于同一个包名下，也要引入
import com.yezi.aidldemo.ser.aidl.IStudentNoticeLisenner;

interface IStudentManager {
     List<Student> getStudentList();

     void addStudent(in Student stu);

     void registerNoticeLisenner(IStudentNoticeLisenner lisenner);

     void unregisterNoticeLisenner(IStudentNoticeLisenner lisenner);

}
