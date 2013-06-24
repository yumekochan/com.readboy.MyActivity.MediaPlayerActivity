/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Workspace\\com.readboy.MyActivity.MediaPlayerActivity\\src\\com\\readboy\\rbpopupservice\\IrbPopupManager.aidl
 */
package com.readboy.rbpopupservice;
public interface IrbPopupManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.readboy.rbpopupservice.IrbPopupManager
{
private static final java.lang.String DESCRIPTOR = "com.readboy.rbpopupservice.IrbPopupManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.readboy.rbpopupservice.IrbPopupManager interface,
 * generating a proxy if needed.
 */
public static com.readboy.rbpopupservice.IrbPopupManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.readboy.rbpopupservice.IrbPopupManager))) {
return ((com.readboy.rbpopupservice.IrbPopupManager)iin);
}
return new com.readboy.rbpopupservice.IrbPopupManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_addWindow:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addWindow(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeWindow:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.removeWindow(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_bringWindowToFront:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.bringWindowToFront(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_setTopWindowFocus:
{
data.enforceInterface(DESCRIPTOR);
this.setTopWindowFocus();
reply.writeNoException();
return true;
}
case TRANSACTION_killTopWindowFocus:
{
data.enforceInterface(DESCRIPTOR);
this.killTopWindowFocus();
reply.writeNoException();
return true;
}
case TRANSACTION_isTopWindow:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isTopWindow(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isBottomWindow:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isBottomWindow(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setOutFlag:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setOutFlag(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_clearOutFlag:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.clearOutFlag(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_checkIsLostAllFocus:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.checkIsLostAllFocus();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_disableAllFocus:
{
data.enforceInterface(DESCRIPTOR);
this.disableAllFocus();
reply.writeNoException();
return true;
}
case TRANSACTION_enableAllFocus:
{
data.enforceInterface(DESCRIPTOR);
this.enableAllFocus();
reply.writeNoException();
return true;
}
case TRANSACTION_updateWindowPos:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.updateWindowPos(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_getTopWindowPos:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getTopWindowPos(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.readboy.rbpopupservice.IrbPopupManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void addWindow(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_addWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeWindow(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_removeWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void bringWindowToFront(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_bringWindowToFront, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setTopWindowFocus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setTopWindowFocus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void killTopWindowFocus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_killTopWindowFocus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isTopWindow(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_isTopWindow, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isBottomWindow(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_isBottomWindow, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setOutFlag(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_setOutFlag, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void clearOutFlag(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_clearOutFlag, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean checkIsLostAllFocus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_checkIsLostAllFocus, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void disableAllFocus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disableAllFocus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void enableAllFocus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_enableAllFocus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void updateWindowPos(java.lang.String tag, int x, int y) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
_data.writeInt(x);
_data.writeInt(y);
mRemote.transact(Stub.TRANSACTION_updateWindowPos, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getTopWindowPos(java.lang.String tag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(tag);
mRemote.transact(Stub.TRANSACTION_getTopWindowPos, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_addWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_removeWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_bringWindowToFront = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setTopWindowFocus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_killTopWindowFocus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_isTopWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_isBottomWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setOutFlag = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_clearOutFlag = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_checkIsLostAllFocus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_disableAllFocus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_enableAllFocus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_updateWindowPos = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getTopWindowPos = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
public void addWindow(java.lang.String tag) throws android.os.RemoteException;
public void removeWindow(java.lang.String tag) throws android.os.RemoteException;
public void bringWindowToFront(java.lang.String tag) throws android.os.RemoteException;
public void setTopWindowFocus() throws android.os.RemoteException;
public void killTopWindowFocus() throws android.os.RemoteException;
public boolean isTopWindow(java.lang.String tag) throws android.os.RemoteException;
public boolean isBottomWindow(java.lang.String tag) throws android.os.RemoteException;
public void setOutFlag(java.lang.String tag) throws android.os.RemoteException;
public void clearOutFlag(java.lang.String tag) throws android.os.RemoteException;
public boolean checkIsLostAllFocus() throws android.os.RemoteException;
public void disableAllFocus() throws android.os.RemoteException;
public void enableAllFocus() throws android.os.RemoteException;
public void updateWindowPos(java.lang.String tag, int x, int y) throws android.os.RemoteException;
public int getTopWindowPos(java.lang.String tag) throws android.os.RemoteException;
}
