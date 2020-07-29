package com.yh.hellobinder.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.yh.hellobinder.Book;
import com.yh.hellobinder.proxy.Proxy;

import java.util.List;

/**
 * 第二步：
 * 只定义服务端具备什么要的能力是不够的，既然是跨进程调用，那么接下来我们得实现一个跨进程调用对象 Stub。
 * Stub 继承 Binder, 说明它是一个 Binder 本地对象；实现 IInterface 接口，表明具有 Server 承诺给 Client 的能力；
 * Stub 是一个抽象类，具体的 IInterface 的相关实现需要调用方自己实现。
 */
public abstract class Stub extends Binder implements BookManager {

    private static final String DESCRIPTOR = "com.yh.hellobinder.server.BookManager";

    Stub() {
        this.attachInterface(this, DESCRIPTOR);
    }

    /**
     * 当 Client 端在创建和服务端的连接，调用 bindService 时需要创建一个 ServiceConnection 对象作为入参。
     * 在 ServiceConnection 的回调方法 onServiceConnected 中 会通过这个 asInterface(IBinder binder)
     * 拿到 BookManager 对象，这个 IBinder 类型的入参 binder 是驱动传给我们的，正如你在代码中看到的一样，
     * 方法中会去调用 binder.queryLocalInterface() 去查找 Binder 本地对象，如果找到了就说明 Client 和 Server 在同一进程，
     * 那么这个 binder 本身就是 Binder 本地对象，可以直接使用。否则说明是 binder 是个远程对象，也就是 BinderProxy。
     * 因此需要我们创建一个代理对象 Proxy，通过这个代理对象来是实现远程访问。
     */
    public static BookManager asInterface(IBinder binder) {
        if (binder == null)
            return null;
        IInterface iin = binder.queryLocalInterface(DESCRIPTOR);
        if (iin instanceof BookManager)
            return (BookManager) iin;
        return new Proxy(binder);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;

            case TRANSAVTION_getBooks:
                //1. 客户端请求前先调用writeInterfaceToken
                //2. 然后客户端调用enforceInterface检查参数是否一样
                //3. 如果不一样，抛出异常"Binder invocation to an incorrect interface"
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBooks();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;

            case TRANSAVTION_addBook:
                data.enforceInterface(DESCRIPTOR);
                Book arg0 = null;
                if (data.readInt() != 0) {
                    arg0 = Book.CREATOR.createFromParcel(data);
                }
                this.addBook(arg0);
                reply.writeNoException();
                return true;

        }
        return super.onTransact(code, data, reply, flags);
    }


    //在 Stub 类中为 BookManager 接口中的每个函数中定义了一个编号，只不过上面的源码中我们简化掉了；
    // 在跨进程调用的时候，不会传递函数而是传递编号来指明要调用哪个函数
    public static final int TRANSAVTION_getBooks = IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSAVTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;
}
