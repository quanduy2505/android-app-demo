package android.support.v4.app;

import android.os.Build.VERSION;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.media.TransportMediator;
import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

final class BackStackRecord extends FragmentTransaction implements BackStackEntry, OpGenerator {
    static final int OP_ADD = 1;
    static final int OP_ATTACH = 7;
    static final int OP_DETACH = 6;
    static final int OP_HIDE = 4;
    static final int OP_NULL = 0;
    static final int OP_REMOVE = 3;
    static final int OP_REPLACE = 2;
    static final int OP_SHOW = 5;
    static final boolean SUPPORTS_TRANSITIONS;
    static final String TAG = "FragmentManager";
    boolean mAddToBackStack;
    boolean mAllowAddToBackStack;
    boolean mAllowOptimization;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    int mIndex;
    final FragmentManagerImpl mManager;
    String mName;
    ArrayList<Op> mOps;
    int mPopEnterAnim;
    int mPopExitAnim;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    int mTransition;
    int mTransitionStyle;

    static final class Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        int popEnterAnim;
        int popExitAnim;

        Op() {
        }
    }

    static {
        SUPPORTS_TRANSITIONS = VERSION.SDK_INT >= 21 ? true : SUPPORTS_TRANSITIONS;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mName != null) {
            sb.append(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
            sb.append(this.mName);
        }
        sb.append("}");
        return sb.toString();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        dump(prefix, writer, true);
    }

    public void dump(String prefix, PrintWriter writer, boolean full) {
        if (full) {
            writer.print(prefix);
            writer.print("mName=");
            writer.print(this.mName);
            writer.print(" mIndex=");
            writer.print(this.mIndex);
            writer.print(" mCommitted=");
            writer.println(this.mCommitted);
            if (this.mTransition != 0) {
                writer.print(prefix);
                writer.print("mTransition=#");
                writer.print(Integer.toHexString(this.mTransition));
                writer.print(" mTransitionStyle=#");
                writer.println(Integer.toHexString(this.mTransitionStyle));
            }
            if (!(this.mEnterAnim == 0 && this.mExitAnim == 0)) {
                writer.print(prefix);
                writer.print("mEnterAnim=#");
                writer.print(Integer.toHexString(this.mEnterAnim));
                writer.print(" mExitAnim=#");
                writer.println(Integer.toHexString(this.mExitAnim));
            }
            if (!(this.mPopEnterAnim == 0 && this.mPopExitAnim == 0)) {
                writer.print(prefix);
                writer.print("mPopEnterAnim=#");
                writer.print(Integer.toHexString(this.mPopEnterAnim));
                writer.print(" mPopExitAnim=#");
                writer.println(Integer.toHexString(this.mPopExitAnim));
            }
            if (!(this.mBreadCrumbTitleRes == 0 && this.mBreadCrumbTitleText == null)) {
                writer.print(prefix);
                writer.print("mBreadCrumbTitleRes=#");
                writer.print(Integer.toHexString(this.mBreadCrumbTitleRes));
                writer.print(" mBreadCrumbTitleText=");
                writer.println(this.mBreadCrumbTitleText);
            }
            if (!(this.mBreadCrumbShortTitleRes == 0 && this.mBreadCrumbShortTitleText == null)) {
                writer.print(prefix);
                writer.print("mBreadCrumbShortTitleRes=#");
                writer.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
                writer.print(" mBreadCrumbShortTitleText=");
                writer.println(this.mBreadCrumbShortTitleText);
            }
        }
        if (!this.mOps.isEmpty()) {
            writer.print(prefix);
            writer.println("Operations:");
            String innerPrefix = prefix + "    ";
            int numOps = this.mOps.size();
            for (int opNum = OP_NULL; opNum < numOps; opNum += OP_ADD) {
                String cmdStr;
                Op op = (Op) this.mOps.get(opNum);
                switch (op.cmd) {
                    case OP_NULL /*0*/:
                        cmdStr = "NULL";
                        break;
                    case OP_ADD /*1*/:
                        cmdStr = "ADD";
                        break;
                    case OP_REPLACE /*2*/:
                        cmdStr = "REPLACE";
                        break;
                    case OP_REMOVE /*3*/:
                        cmdStr = "REMOVE";
                        break;
                    case OP_HIDE /*4*/:
                        cmdStr = "HIDE";
                        break;
                    case OP_SHOW /*5*/:
                        cmdStr = "SHOW";
                        break;
                    case OP_DETACH /*6*/:
                        cmdStr = "DETACH";
                        break;
                    case OP_ATTACH /*7*/:
                        cmdStr = "ATTACH";
                        break;
                    default:
                        cmdStr = "cmd=" + op.cmd;
                        break;
                }
                writer.print(prefix);
                writer.print("  Op #");
                writer.print(opNum);
                writer.print(": ");
                writer.print(cmdStr);
                writer.print(MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                writer.println(op.fragment);
                if (full) {
                    if (!(op.enterAnim == 0 && op.exitAnim == 0)) {
                        writer.print(prefix);
                        writer.print("enterAnim=#");
                        writer.print(Integer.toHexString(op.enterAnim));
                        writer.print(" exitAnim=#");
                        writer.println(Integer.toHexString(op.exitAnim));
                    }
                    if (op.popEnterAnim != 0 || op.popExitAnim != 0) {
                        writer.print(prefix);
                        writer.print("popEnterAnim=#");
                        writer.print(Integer.toHexString(op.popEnterAnim));
                        writer.print(" popExitAnim=#");
                        writer.println(Integer.toHexString(op.popExitAnim));
                    }
                }
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl manager) {
        this.mOps = new ArrayList();
        this.mAllowAddToBackStack = true;
        this.mIndex = -1;
        this.mAllowOptimization = SUPPORTS_TRANSITIONS;
        this.mManager = manager;
    }

    public int getId() {
        return this.mIndex;
    }

    public int getBreadCrumbTitleRes() {
        return this.mBreadCrumbTitleRes;
    }

    public int getBreadCrumbShortTitleRes() {
        return this.mBreadCrumbShortTitleRes;
    }

    public CharSequence getBreadCrumbTitle() {
        if (this.mBreadCrumbTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes);
        }
        return this.mBreadCrumbTitleText;
    }

    public CharSequence getBreadCrumbShortTitle() {
        if (this.mBreadCrumbShortTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes);
        }
        return this.mBreadCrumbShortTitleText;
    }

    void addOp(Op op) {
        this.mOps.add(op);
        op.enterAnim = this.mEnterAnim;
        op.exitAnim = this.mExitAnim;
        op.popEnterAnim = this.mPopEnterAnim;
        op.popExitAnim = this.mPopExitAnim;
    }

    public FragmentTransaction add(Fragment fragment, String tag) {
        doAddOp(OP_NULL, fragment, tag, OP_ADD);
        return this;
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment) {
        doAddOp(containerViewId, fragment, null, OP_ADD);
        return this;
    }

    public FragmentTransaction add(int containerViewId, Fragment fragment, String tag) {
        doAddOp(containerViewId, fragment, tag, OP_ADD);
        return this;
    }

    private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd) {
        Class fragmentClass = fragment.getClass();
        int modifiers = fragmentClass.getModifiers();
        if (fragmentClass.isAnonymousClass() || !Modifier.isPublic(modifiers) || (fragmentClass.isMemberClass() && !Modifier.isStatic(modifiers))) {
            throw new IllegalStateException("Fragment " + fragmentClass.getCanonicalName() + " must be a public static class to be  properly recreated from" + " instance state.");
        }
        fragment.mFragmentManager = this.mManager;
        if (tag != null) {
            if (fragment.mTag == null || tag.equals(fragment.mTag)) {
                fragment.mTag = tag;
            } else {
                throw new IllegalStateException("Can't change tag of fragment " + fragment + ": was " + fragment.mTag + " now " + tag);
            }
        }
        if (containerViewId != 0) {
            if (containerViewId == -1) {
                throw new IllegalArgumentException("Can't add fragment " + fragment + " with tag " + tag + " to container view with no id");
            } else if (fragment.mFragmentId == 0 || fragment.mFragmentId == containerViewId) {
                fragment.mFragmentId = containerViewId;
                fragment.mContainerId = containerViewId;
            } else {
                throw new IllegalStateException("Can't change container ID of fragment " + fragment + ": was " + fragment.mFragmentId + " now " + containerViewId);
            }
        }
        Op op = new Op();
        op.cmd = opcmd;
        op.fragment = fragment;
        addOp(op);
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment) {
        return replace(containerViewId, fragment, null);
    }

    public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
        if (containerViewId == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }
        doAddOp(containerViewId, fragment, tag, OP_REPLACE);
        return this;
    }

    public FragmentTransaction remove(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_REMOVE;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction hide(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_HIDE;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction show(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_SHOW;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction detach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_DETACH;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction attach(Fragment fragment) {
        Op op = new Op();
        op.cmd = OP_ATTACH;
        op.fragment = fragment;
        addOp(op);
        return this;
    }

    public FragmentTransaction setCustomAnimations(int enter, int exit) {
        return setCustomAnimations(enter, exit, OP_NULL, OP_NULL);
    }

    public FragmentTransaction setCustomAnimations(int enter, int exit, int popEnter, int popExit) {
        this.mEnterAnim = enter;
        this.mExitAnim = exit;
        this.mPopEnterAnim = popEnter;
        this.mPopExitAnim = popExit;
        return this;
    }

    public FragmentTransaction setTransition(int transition) {
        this.mTransition = transition;
        return this;
    }

    public FragmentTransaction addSharedElement(View sharedElement, String name) {
        if (SUPPORTS_TRANSITIONS) {
            String transitionName = ViewCompat.getTransitionName(sharedElement);
            if (transitionName == null) {
                throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
            }
            if (this.mSharedElementSourceNames == null) {
                this.mSharedElementSourceNames = new ArrayList();
                this.mSharedElementTargetNames = new ArrayList();
            } else if (this.mSharedElementTargetNames.contains(name)) {
                throw new IllegalArgumentException("A shared element with the target name '" + name + "' has already been added to the transaction.");
            } else if (this.mSharedElementSourceNames.contains(transitionName)) {
                throw new IllegalArgumentException("A shared element with the source name '" + transitionName + " has already been added to the transaction.");
            }
            this.mSharedElementSourceNames.add(transitionName);
            this.mSharedElementTargetNames.add(name);
        }
        return this;
    }

    public FragmentTransaction setTransitionStyle(int styleRes) {
        this.mTransitionStyle = styleRes;
        return this;
    }

    public FragmentTransaction addToBackStack(String name) {
        if (this.mAllowAddToBackStack) {
            this.mAddToBackStack = true;
            this.mName = name;
            return this;
        }
        throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }

    public boolean isAddToBackStackAllowed() {
        return this.mAllowAddToBackStack;
    }

    public FragmentTransaction disallowAddToBackStack() {
        if (this.mAddToBackStack) {
            throw new IllegalStateException("This transaction is already being added to the back stack");
        }
        this.mAllowAddToBackStack = SUPPORTS_TRANSITIONS;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(int res) {
        this.mBreadCrumbTitleRes = res;
        this.mBreadCrumbTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(CharSequence text) {
        this.mBreadCrumbTitleRes = OP_NULL;
        this.mBreadCrumbTitleText = text;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(int res) {
        this.mBreadCrumbShortTitleRes = res;
        this.mBreadCrumbShortTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(CharSequence text) {
        this.mBreadCrumbShortTitleRes = OP_NULL;
        this.mBreadCrumbShortTitleText = text;
        return this;
    }

    void bumpBackStackNesting(int amt) {
        if (this.mAddToBackStack) {
            if (FragmentManagerImpl.DEBUG) {
                Log.v(TAG, "Bump nesting in " + this + " by " + amt);
            }
            int numOps = this.mOps.size();
            for (int opNum = OP_NULL; opNum < numOps; opNum += OP_ADD) {
                Op op = (Op) this.mOps.get(opNum);
                if (op.fragment != null) {
                    Fragment fragment = op.fragment;
                    fragment.mBackStackNesting += amt;
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v(TAG, "Bump nesting of " + op.fragment + " to " + op.fragment.mBackStackNesting);
                    }
                }
            }
        }
    }

    public int commit() {
        return commitInternal(SUPPORTS_TRANSITIONS);
    }

    public int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    public void commitNow() {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, SUPPORTS_TRANSITIONS);
    }

    public void commitNowAllowingStateLoss() {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, true);
    }

    public FragmentTransaction setAllowOptimization(boolean allowOptimization) {
        this.mAllowOptimization = allowOptimization;
        return this;
    }

    int commitInternal(boolean allowStateLoss) {
        if (this.mCommitted) {
            throw new IllegalStateException("commit already called");
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "Commit: " + this);
            dump("  ", null, new PrintWriter(new LogWriter(TAG)), null);
        }
        this.mCommitted = true;
        if (this.mAddToBackStack) {
            this.mIndex = this.mManager.allocBackStackIndex(this);
        } else {
            this.mIndex = -1;
        }
        this.mManager.enqueueAction(this, allowStateLoss);
        return this.mIndex;
    }

    public boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v(TAG, "Run: " + this);
        }
        records.add(this);
        isRecordPop.add(Boolean.valueOf(SUPPORTS_TRANSITIONS));
        if (this.mAddToBackStack) {
            this.mManager.addBackStackState(this);
        }
        return true;
    }

    boolean interactsWith(int containerId) {
        int numOps = this.mOps.size();
        for (int opNum = OP_NULL; opNum < numOps; opNum += OP_ADD) {
            if (((Op) this.mOps.get(opNum)).fragment.mContainerId == containerId) {
                return true;
            }
        }
        return SUPPORTS_TRANSITIONS;
    }

    boolean interactsWith(ArrayList<BackStackRecord> records, int startIndex, int endIndex) {
        if (endIndex == startIndex) {
            return SUPPORTS_TRANSITIONS;
        }
        int numOps = this.mOps.size();
        int lastContainer = -1;
        for (int opNum = OP_NULL; opNum < numOps; opNum += OP_ADD) {
            int container = ((Op) this.mOps.get(opNum)).fragment.mContainerId;
            if (!(container == 0 || container == lastContainer)) {
                lastContainer = container;
                for (int i = startIndex; i < endIndex; i += OP_ADD) {
                    BackStackRecord record = (BackStackRecord) records.get(i);
                    int numThoseOps = record.mOps.size();
                    for (int thoseOpIndex = OP_NULL; thoseOpIndex < numThoseOps; thoseOpIndex += OP_ADD) {
                        if (((Op) record.mOps.get(thoseOpIndex)).fragment.mContainerId == container) {
                            return true;
                        }
                    }
                }
                continue;
            }
        }
        return SUPPORTS_TRANSITIONS;
    }

    void executeOps() {
        int numOps = this.mOps.size();
        for (int opNum = OP_NULL; opNum < numOps; opNum += OP_ADD) {
            Op op = (Op) this.mOps.get(opNum);
            Fragment f = op.fragment;
            f.setNextTransition(this.mTransition, this.mTransitionStyle);
            switch (op.cmd) {
                case OP_ADD /*1*/:
                    f.setNextAnim(op.enterAnim);
                    this.mManager.addFragment(f, SUPPORTS_TRANSITIONS);
                    break;
                case OP_REMOVE /*3*/:
                    f.setNextAnim(op.exitAnim);
                    this.mManager.removeFragment(f);
                    break;
                case OP_HIDE /*4*/:
                    f.setNextAnim(op.exitAnim);
                    this.mManager.hideFragment(f);
                    break;
                case OP_SHOW /*5*/:
                    f.setNextAnim(op.enterAnim);
                    this.mManager.showFragment(f);
                    break;
                case OP_DETACH /*6*/:
                    f.setNextAnim(op.exitAnim);
                    this.mManager.detachFragment(f);
                    break;
                case OP_ATTACH /*7*/:
                    f.setNextAnim(op.enterAnim);
                    this.mManager.attachFragment(f);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
            if (!(this.mAllowOptimization || op.cmd == OP_ADD)) {
                this.mManager.moveFragmentToExpectedState(f);
            }
        }
        if (!this.mAllowOptimization) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    void executePopOps() {
        for (int opNum = this.mOps.size() - 1; opNum >= 0; opNum--) {
            Op op = (Op) this.mOps.get(opNum);
            Fragment f = op.fragment;
            f.setNextTransition(FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
            switch (op.cmd) {
                case OP_ADD /*1*/:
                    f.setNextAnim(op.popExitAnim);
                    this.mManager.removeFragment(f);
                    break;
                case OP_REMOVE /*3*/:
                    f.setNextAnim(op.popEnterAnim);
                    this.mManager.addFragment(f, SUPPORTS_TRANSITIONS);
                    break;
                case OP_HIDE /*4*/:
                    f.setNextAnim(op.popEnterAnim);
                    this.mManager.showFragment(f);
                    break;
                case OP_SHOW /*5*/:
                    f.setNextAnim(op.popExitAnim);
                    this.mManager.hideFragment(f);
                    break;
                case OP_DETACH /*6*/:
                    f.setNextAnim(op.popEnterAnim);
                    this.mManager.attachFragment(f);
                    break;
                case OP_ATTACH /*7*/:
                    f.setNextAnim(op.popExitAnim);
                    this.mManager.detachFragment(f);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
            }
            if (!(this.mAllowOptimization || op.cmd == OP_REMOVE)) {
                this.mManager.moveFragmentToExpectedState(f);
            }
        }
        if (!this.mAllowOptimization) {
            this.mManager.moveToState(this.mManager.mCurState, true);
        }
    }

    void expandReplaceOps(ArrayList<Fragment> added) {
        int opNum = OP_NULL;
        while (opNum < this.mOps.size()) {
            Op op = (Op) this.mOps.get(opNum);
            switch (op.cmd) {
                case OP_ADD /*1*/:
                case OP_ATTACH /*7*/:
                    added.add(op.fragment);
                    break;
                case OP_REPLACE /*2*/:
                    Fragment f = op.fragment;
                    int containerId = f.mContainerId;
                    boolean alreadyAdded = SUPPORTS_TRANSITIONS;
                    for (int i = added.size() - 1; i >= 0; i--) {
                        Fragment old = (Fragment) added.get(i);
                        if (old.mContainerId == containerId) {
                            if (old == f) {
                                alreadyAdded = true;
                            } else {
                                Op removeOp = new Op();
                                removeOp.cmd = OP_REMOVE;
                                removeOp.fragment = old;
                                removeOp.enterAnim = op.enterAnim;
                                removeOp.popEnterAnim = op.popEnterAnim;
                                removeOp.exitAnim = op.exitAnim;
                                removeOp.popExitAnim = op.popExitAnim;
                                this.mOps.add(opNum, removeOp);
                                added.remove(old);
                                opNum += OP_ADD;
                            }
                        }
                    }
                    if (!alreadyAdded) {
                        op.cmd = OP_ADD;
                        added.add(f);
                        break;
                    }
                    this.mOps.remove(opNum);
                    opNum--;
                    break;
                case OP_REMOVE /*3*/:
                case OP_DETACH /*6*/:
                    added.remove(op.fragment);
                    break;
                default:
                    break;
            }
            opNum += OP_ADD;
        }
    }

    void trackAddedFragmentsInPop(ArrayList<Fragment> added) {
        for (int opNum = OP_NULL; opNum < this.mOps.size(); opNum += OP_ADD) {
            Op op = (Op) this.mOps.get(opNum);
            switch (op.cmd) {
                case OP_ADD /*1*/:
                case OP_ATTACH /*7*/:
                    added.remove(op.fragment);
                    break;
                case OP_REMOVE /*3*/:
                case OP_DETACH /*6*/:
                    added.add(op.fragment);
                    break;
                default:
                    break;
            }
        }
    }

    boolean isPostponed() {
        for (int opNum = OP_NULL; opNum < this.mOps.size(); opNum += OP_ADD) {
            if (isFragmentPostponed((Op) this.mOps.get(opNum))) {
                return true;
            }
        }
        return SUPPORTS_TRANSITIONS;
    }

    void setOnStartPostponedListener(OnStartEnterTransitionListener listener) {
        for (int opNum = OP_NULL; opNum < this.mOps.size(); opNum += OP_ADD) {
            Op op = (Op) this.mOps.get(opNum);
            if (isFragmentPostponed(op)) {
                op.fragment.setOnStartEnterTransitionListener(listener);
            }
        }
    }

    private static boolean isFragmentPostponed(Op op) {
        Fragment fragment = op.fragment;
        return (!fragment.mAdded || fragment.mView == null || fragment.mDetached || fragment.mHidden || !fragment.isPostponed()) ? SUPPORTS_TRANSITIONS : true;
    }

    public String getName() {
        return this.mName;
    }

    public int getTransition() {
        return this.mTransition;
    }

    public int getTransitionStyle() {
        return this.mTransitionStyle;
    }

    public boolean isEmpty() {
        return this.mOps.isEmpty();
    }
}
