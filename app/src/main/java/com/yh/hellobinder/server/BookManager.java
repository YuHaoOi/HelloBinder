package com.yh.hellobinder.server;

import android.os.IInterface;
import android.os.RemoteException;

import com.yh.hellobinder.Book;

import java.util.List;

/**
 * 第一步：
 * 那么服务端进程具备什么样的能力？能为客户端提供什么样的服务呢？
 * 还记得我们前面介绍过的 IInterface 吗，它代表的就是服务端进程具体什么样的能力。
 * 因此我们需要定义一个 BookManager 接口，BookManager 继承自 IIterface，表明服务端具备什么样的能力。
 */
public interface BookManager extends IInterface {

    List<Book> getBooks() throws RemoteException;

    void addBook(Book book) throws RemoteException;
}
