/* (c) 2019 by Panayotis Katsaloulis
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrossMobile; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package crossmobile.ios.uikit;

import crossmobile.ios.coregraphics.CGAffineTransform;
import crossmobile.ios.coregraphics.CGRect;
import crossmobile.ios.coregraphics.CGSize;
import crossmobile.ios.foundation.NSBundle;
import crossmobile.ios.foundation.NSExtensionContext;
import org.crossmobile.bind.graphics.DrawableMetrics;
import org.crossmobile.bind.graphics.GraphicsBridgeConstants;
import org.crossmobile.bridge.Native;
import org.crossmobile.bridge.ann.*;
import org.crossmobile.support.cassowary.ClVariable;
import org.robovm.objc.block.VoidBlock1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UIViewController class defines an object that handles all the views that
 * constitute the interface of an application. UIViewController manages all the
 * other controllers since it is the base class of the view controller
 * hierarchy.
 */
@CMClass
public class UIViewController extends UIResponder {

    private final String nibName;
    private final NSExtensionContext extensionContext = null;
    private final NSBundle bundlename;
    UITabBarItem tabBarItem;
    private UIView view;
    private boolean wantsFullScreenLayout;
    private UINavigationItem navigationItem;
    private UIBarButtonItem editButtonItem;
    private boolean editing;
    private boolean hidesBottomBarWhenPushed;
    private List<UIBarButtonItem> toolbarItems;
    private UIViewController pcontroller;
    private String title;
    private int modalPresentationStyle = UIModalPresentationStyle.FullScreen;
    private int modalTransitionStyle = UIModalTransitionStyle.CoverVertical;
    private boolean modalInPopover = false;
    private CGSize contentSizeForViewInPopover;
    private int edgesForExtendedLayout;
    private boolean automaticallyAdjustsScrollViewInsets = true;
    private UIViewController modalViewController;
    private boolean scrollWasAlreadySearched = false;
    private UIScrollView firstScroll = null;
    private UIEdgeInsets additionalSafeAreaInsets = UIEdgeInsets.zero();
    private UILayoutSupport bottomLayoutGuide;
    private UILayoutSupport topLayoutGuide;
    private List<NSLayoutConstraint> layoutSupportConstraints;
    private boolean definesPresentationContext;
    private boolean providesPresentationContextTransitionStyle = false;
    private boolean disablesAutomaticKeyboardDismissal = true;
    private Map<String, UIStoryboardSegue> segueMap;
    private UIStoryboard storyboard;
    private List<UIViewController> childViewControllers;
    private UIViewController presentingViewController;
    private String restorationIdentifier;

    /**
     * Constructs a default view controller.
     */
    public UIViewController() {
        this(null, null);
    }

    /**
     * Constructs a view controller according to the description of the nib file
     * that is located in the specified bundle.
     *
     * @param nibName    The name of the nib file.If NULL then it uses no nib file.
     * @param bundlename The bundle in which the nib file is located.
     */
    @CMConstructor("- (instancetype)initWithNibName:(NSString *)nibNameOrNil \n"
            + "                         bundle:(NSBundle *)nibBundleOrNil;")
    public UIViewController(String nibName, NSBundle bundlename) {
        editing = false;
        this.nibName = nibName;
        this.bundlename = bundlename;
    }

    /**
     * Rotates all the views to comply with the rotation of the device, if this
     * is possible.
     */
    @CMSelector("+ (void)attemptRotationToDeviceOrientation;")
    public static void attemptRotationToDeviceOrientation() {
    }

    @CMGetter("@property(nonatomic, readonly, strong) UIStoryboard *storyboard;")
    public UIStoryboard storyboard() {
        return storyboard;
    }

    void setStoryboard(UIStoryboard storyboard) {
        this.storyboard = storyboard;
    }

    @CMSelector("- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier \n"
            + "                                  sender:(id)sender;")
    public boolean shouldPerformSegueWithIdentifier(String identifier, Object sender) {
        return true;
    }

    @CMSelector("- (void)prepareForSegue:(UIStoryboardSegue *)segue \n"
            + "                 sender:(id)sender;")
    public void prepareForSegue(UIStoryboardSegue segue, Object sender) {

    }

    @CMSelector("- (void)performSegueWithIdentifier:(NSString *)identifier \n"
            + "                            sender:(id)sender;")
    public void performSegueWithIdentifier(String identifier, Object sender) {
        if (segueMap != null) {
            UIStoryboardSegue segue = segueMap.get(identifier);
            if (segue != null) {
                segue.perform();
                return;
            }
        }
        Native.system().error("'Receiver " + this.toString() + " has no segue with identifier '" + identifier + "''", null);
    }

    @CMSelector("- (NSArray<UIViewController *> *)allowedChildViewControllersForUnwindingFromSource:(UIStoryboardUnwindSegueSource *)source;")
    public List<UIViewController> allowedChildViewControllersForUnwindingFromSource(UIStoryboardUnwindSegueSource source) {
        List<UIViewController> allowed = new ArrayList<>(childViewControllers);
        UIViewController c = childViewControllerContainingSegueSource(source);
        if (c != null)
            allowed.remove(c);
        return allowed;
    }

    @CMSelector("- (UIViewController *)childViewControllerContainingSegueSource:(UIStoryboardUnwindSegueSource *)source;")
    public UIViewController childViewControllerContainingSegueSource(UIStoryboardUnwindSegueSource source) {
        return null;
    }

    void addSegue(String id, UIStoryboardSegue segue) {
        if (id == null || segue == null)
            return;
        if (segueMap == null)
            segueMap = new HashMap<>();
        segueMap.put(id, segue);
    }

    @Override
    public UIResponder nextResponder() {
        return view().superview();
    }

    /**
     * Returns a Boolean that shows whether the view is loaded into memory.
     *
     * @return A Boolean that shows whether the view is loaded into memory.
     */
    @CMGetter("@property(nonatomic, readonly, getter=isViewLoaded) BOOL viewLoaded;")
    public boolean isViewLoaded() {
        return view != null;
    }

    /**
     * Return controllers view if it is already loaded. It does not call loadView
     *
     * @return the current view, or null
     */
    @CMGetter("@property(nonatomic, readonly, strong) UIView *viewIfLoaded;")
    public UIView viewIfLoaded() {
        return view;
    }

    /**
     * Returns the header view that should be rotated after the interface
     * rotation.
     *
     * @return The header view that should be rotated.Null if there is no header
     * view.
     */
//    @Deprecated //Header views are now animated with the rest of the view.
    @CMSelector("- (UIView *)rotatingHeaderView;")
    @Deprecated
    public UIView rotatingHeaderView() {
        return null;
    }

    /**
     * Returns the footer view that should be rotated after the interface
     * rotation.
     *
     * @return The footer view that should be rotated after the interface
     * rotation.
     */
    //@Deprecated //Footer views are now animated with the rest of the view.
    @CMSelector("- (UIView *)rotatingFooterView;")
    @Deprecated
    public UIView rotatingFooterView() {
        return null;
    }

    /**
     * Returns the presentation style. Used by modally presented view
     * controllers.
     *
     * @return The presentation style of modally presented view controllers.
     * @see crossmobile.ios.uikit.UIModalPresentationStyle
     */
    @CMGetter("@property(nonatomic, assign) UIModalPresentationStyle modalPresentationStyle;")
    public int modalPresentationStyle() {
        return modalPresentationStyle;
    }

    /**
     * Sets the presentation style. Used by modally presented view controllers.
     *
     * @param UIModalPresentationStyle The presentation style of modally
     *                                 presented view controllers.
     * @see crossmobile.ios.uikit.UIModalPresentationStyle
     */
    @CMSetter("@property(nonatomic, assign) UIModalPresentationStyle modalPresentationStyle;")
    public void setModalPresentationStyle(int UIModalPresentationStyle) {
        this.modalPresentationStyle = UIModalPresentationStyle;
    }

    /**
     * Returns the transition style.
     *
     * @return The transition style.
     * @see crossmobile.ios.uikit.UIModalTransitionStyle
     */
    @CMGetter("@property(nonatomic, assign) UIModalTransitionStyle modalTransitionStyle;")
    public int modalTransitionStyle() {
        return modalTransitionStyle;
    }

    /**
     * Sets the transition style.
     *
     * @param UIModalTransitionStyle The transition style.
     * @see crossmobile.ios.uikit.UIModalTransitionStyle
     */
    @CMSetter("@property(nonatomic, assign) UIModalTransitionStyle modalTransitionStyle;")
    public void setModalTransitionStyle(int UIModalTransitionStyle) {
        this.modalTransitionStyle = UIModalTransitionStyle;
    }

    /**
     * The default value of this field is false. If true the owning controller
     * does not allow user interactions outside of the modal controllers view
     *
     * @return modalInPopover field's value
     */
    @CMGetter("@property(nonatomic, readwrite, getter=isModalInPopover) BOOL modalInPopover;")
    public boolean isModalInPopover() {
        return modalInPopover;
    }

    /**
     * The default value of this field is false. If true the owning controller
     * does not allow user interactions outside of the modal controllers view
     *
     * @param modalInPopover Define if interactions are enabled or if the pop-over is modal
     */
    @CMSetter("@property(nonatomic, readwrite, getter=isModalInPopover) BOOL modalInPopover;")
    public void setModalInPopover(boolean modalInPopover) {
        this.modalInPopover = modalInPopover;
    }

    /**
     * Returns the view of this view controller.
     *
     * @return The view of this view controller.
     */
    @CMGetter("@property(nonatomic, strong) UIView *view;")
    public UIView view() {
        loadViewIfNeeded();
        return view;
    }

    /**
     * Loads the controller's view, if it is not loaded yet
     */
    @CMSelector("- (void)loadViewIfNeeded;")
    public void loadViewIfNeeded() {
        if (view == null) {
            loadView();
            view.controller = this;
            view.setFrame(UIScreen.mainScreen().applicationFrame());
            resetLayoutSupport();
            viewDidLoad();
        }
    }

    /**
     * Sets the view of this view controller.
     *
     * @param view The view of this view controller.
     */
    @CMSetter("@property(nonatomic, strong) UIView *view;")
    public void setView(UIView view) {
        if (this.view != null) // Old
            view.controller = null;
        boolean viewHasChanged = this.view != view;
        this.view = view;
        if (viewHasChanged)
            resetLayoutSupport();
        if (this.view != null) // New
            view.controller = this;
    }

    /**
     * Returns the title of the view for this view controller.
     *
     * @return The title of the view for this view controller.
     */
    @CMGetter("@property(nonatomic, copy) NSString *title;")
    public String title() {
        return title;
    }

    /**
     * Sets the title for the view for this view controller.
     *
     * @param title The title of the view for this view controller.
     */
    @CMSetter("@property(nonatomic, copy) NSString *title;")
    public void setTitle(String title) {
        this.title = title;
        if (tabBarItem != null)
            tabBarItem.setTitle(title);
        if (navigationItem != null)
            navigationItem.setTitle(title);
    }

    /**
     * Returns the navigation item that represents this view controller in its
     * parent navigation bar.
     *
     * @return The navigation item that represents this view controller in its
     * parent navigation bar.
     */
    @CMGetter("@property(nonatomic, readonly, strong) UINavigationItem *navigationItem;")
    public UINavigationItem navigationItem() {
        if (navigationItem == null)
            navigationItem = new UINavigationItem(title);
        return navigationItem;
    }

    /**
     * Returns a Boolean that shows whether the view of this view controller
     * should cover the status bar in full screen.
     *
     * @return A Boolean that shows whether the view should cover the status
     * bar.
     */
    @Deprecated
    @CMGetter("@property(nonatomic, assign) BOOL wantsFullScreenLayout;")
    public boolean wantsFullScreenLayout() {
        return wantsFullScreenLayout;
    }

    /**
     * Sets a Boolean that defines whether the view of this view controller
     * should cover the status bar in full screen.
     *
     * @param wantsFullScreenLayout A Boolean that shows whether the view should
     *                              cover the status bar.
     */
    @Deprecated
    @CMSetter("@property(nonatomic, assign) BOOL wantsFullScreenLayout;")
    public void setWantsFullScreenLayout(boolean wantsFullScreenLayout) {
        this.wantsFullScreenLayout = wantsFullScreenLayout;
    }


    /**
     * Returns the button item of the bar that switches between Edit and Done
     * states.
     *
     * @return Returns the button item of the bar that switches between Edit and
     * Done states.
     */
    @CMSelector("- (UIBarButtonItem *)editButtonItem;")
    public UIBarButtonItem editButtonItem() {
        return editButtonItem;
    }

    /**
     * Returns a Boolean that shows whether the content of the view is currently
     * allowed to be edit by the user.
     *
     * @return A Boolean that shows whether the content of the view is currently
     * allowed to be edit by the user.
     */
    @CMGetter("@property(nonatomic, getter=isEditing) BOOL editing;")
    public boolean isEditing() {
        return editing;
    }

    /**
     * Sets a Boolean that define whether the content of the view is allowed to
     * be currently editable by the user.
     *
     * @param editing A Boolean that indicates whether the content of the view
     *                is currently allowed to be edit by the user.
     */
    @CMSetter("@property(nonatomic, getter=isEditing) BOOL editing;")
    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    /*
     * Simulator ignores animations
     */

    /**
     * Sets values that define whether the content of the content is allowed to
     * be currently editable by the user.
     *
     * @param editing  If TRUE the view is editable.
     * @param animated If TRUE the change is animated.
     */
    @CMSelector("- (void)setEditing:(BOOL)editing \n"
            + "          animated:(BOOL)animated;")
    public void setEditing(boolean editing, boolean animated) {
    }

    /*
     * Simulator ignores animations
     */

    /**
     * Returns a Boolean that shows whether toolbar at the bottom of the screen
     * is visible when the view controller is pushed on to the navigation
     * controller.
     *
     * @return A Boolean that shows whether toolbar at the bottom of the screen
     * is visible when the view controller is pushed on to the navigation
     * controller.
     */
    @CMGetter("@property(nonatomic) BOOL hidesBottomBarWhenPushed;")
    public boolean hidesBottomBarWhenPushed() {
        return hidesBottomBarWhenPushed;
    }

    /**
     * Sets a Boolean that defines whether toolbar at the bottom of the screen
     * is visible when the view controller is pushed on to the navigation
     * controller.
     *
     * @param hidesBottomBarWhenPushed A Boolean that defines whether toolbar at
     *                                 the bottom of the screen is visible when the view controller is pushed on
     *                                 to the navigation controller.
     */
    @CMSetter("@property(nonatomic) BOOL hidesBottomBarWhenPushed;")
    public void setHidesBottomBarWhenPushed(boolean hidesBottomBarWhenPushed) {
        this.hidesBottomBarWhenPushed = hidesBottomBarWhenPushed;
    }

    /**
     * Returns the tab bar item for this view controller when added to a tab bar
     * controller.
     *
     * @return The tab bar item for this view controller when added to a tab bar
     * controller.
     */
    @CMGetter("@property(nonatomic, strong) UITabBarItem *tabBarItem;")
    public UITabBarItem tabBarItem() {
        if (tabBarItem == null) {
            tabBarItem = new UITabBarItem();
            tabBarItem.setTitle(title());
        }
        return tabBarItem;
    }

    /**
     * Sets tab bar item for this view controller when added to a tab bar
     * controller.
     *
     * @param tabBarItem The tab bar item for this view controller when added to
     *                   a tab bar controller.
     */
    @CMSetter("@property(nonatomic, strong) UITabBarItem *tabBarItem;")
    public void setTabBarItem(UITabBarItem tabBarItem) {
        this.tabBarItem = tabBarItem;
    }

    /**
     * Returns the toolbar items list of this view controller.
     *
     * @return The toolbar items list of this view controller.
     */
    @CMGetter("@property(nonatomic, strong) NSArray<__kindof UIBarButtonItem *> *toolbarItems;")
    public List<UIBarButtonItem> toolbarItems() {
        return toolbarItems;
    }

    /**
     * Sets toolbar items list for this view controller.
     *
     * @param items The toolbar items list of this view controller.
     */
    @CMSetter("@property(nonatomic, strong) NSArray<__kindof UIBarButtonItem *> *toolbarItems;")
    public void setToolbarItems(List<UIBarButtonItem> items) {
        setToolbarItems(items, false);
    }

    /**
     * Sets the toolbar items list to be displayed with the view controller
     * using animation or not.
     *
     * @param items    The toolbar items to be displayed.
     * @param animated If set TRUE the change is animated.
     */
    @CMSelector("- (void)setToolbarItems:(NSArray<UIBarButtonItem *> *)toolbarItems \n"
            + "               animated:(BOOL)animated;")
    public void setToolbarItems(List<UIBarButtonItem> items, boolean animated) {
        this.toolbarItems = items;
        if (pcontroller != null && pcontroller instanceof UINavigationController) {
            UINavigationController nc = (UINavigationController) pcontroller;
            if (nc.topViewController() == this)
                nc.toolbar().setItems(items, animated);
        }
    }

    /**
     * Returns the current orientation of the interface when the view controller
     * is in full screen.
     *
     * @return The current orientation of the interface when the view controller
     * is in full screen.
     * @see crossmobile.ios.uikit.UIInterfaceOrientation
     */
    @Deprecated
    @CMGetter("@property(nonatomic, readonly) UIInterfaceOrientation interfaceOrientation;")
    public int interfaceOrientation() {
        Native.system().debug("Return device orientation instead of interface orientation", null);
        return UIDevice.currentDevice().orientation();
    }

    @CMGetter("@property(nonatomic, readonly) BOOL disablesAutomaticKeyboardDismissal;")
    public boolean disablesAutomaticKeyboardDismissal() {
        return disablesAutomaticKeyboardDismissal;
    }

    @CMGetter("@property(nonatomic, assign) BOOL providesPresentationContextTransitionStyle;")
    public boolean providesPresentationContextTransitionStyle() {
        return providesPresentationContextTransitionStyle;
    }

    @CMSetter("@property(nonatomic, assign) BOOL providesPresentationContextTransitionStyle;")
    public void setProvidesPresentationContextTransitionStyle(boolean providesPresentationContextTransitionStyle) {
        this.providesPresentationContextTransitionStyle = providesPresentationContextTransitionStyle;
    }

    @CMGetter("@property(nonatomic, assign) BOOL definesPresentationContext;")
    public boolean definesPresentationContext() {
        return definesPresentationContext;
    }

    @CMSetter("@property(nonatomic, assign) BOOL definesPresentationContext;")
    public void setDefinesPresentationContext(boolean definesPresentationContext) {
        this.definesPresentationContext = definesPresentationContext;
    }

    /**
     * Shows a view Controller
     *
     * @param vc     View Controller to show
     * @param sender Object that requested the View Controller
     */
    @CMSelector("- (void)showViewController:(UIViewController *)vc sender:(id)sender;")
    public void showViewController(UIViewController vc, Object sender) {
        if (parentViewController() != null)
            parentViewController().showViewController(vc, sender);
        else {
            presentViewController(vc, true, null);
        }
    }

    @CMSelector("- (void)showDetailViewController:(UIViewController *)vc sender:(id)sender;")
    public void showDetailViewController(UIViewController vc, Object sender) {
        if (parentViewController() != null)
            parentViewController().showDetailViewController(vc, sender);
        else
            UIApplication.sharedApplication().keyWindow().addSubview(vc.view());
    }

    /**
     * Presents the view that is managed by the specified modal view controller
     * using animation or not according to the parameter.
     *
     * @param viewControllerToPresent The view controller to be presented
     * @param flag                    if true the transition is animated
     * @param completion              block that runs after the presentation ends. can be
     *                                NULL
     */
    @CMSelector("- (void)presentViewController:(UIViewController *)viewControllerToPresent\n"
            + "    animated:(BOOL)flag\n"
            + "    completion:(void (^)(void))completion;")
    public void presentViewController(UIViewController viewControllerToPresent, boolean flag, Runnable completion) {
        this.modalViewController = viewControllerToPresent;
        if (viewControllerToPresent != null)
            Native.system().postOnEventThread(() -> {
                viewControllerToPresent.presentingViewController = this;
                viewControllerToPresent.view().setTransform(CGAffineTransform.makeTranslation(0, viewControllerToPresent.view().getHeight()));
                Runnable animation = () -> {
                    UIApplication.sharedApplication().keyWindow().rootViewController().view().addSubview(viewControllerToPresent.view());
                    viewControllerToPresent.view().setTransform(CGAffineTransform.identity());
                };
                viewControllerToPresent.viewWillAppear(flag);
                viewControllerToPresent.viewSafeAreaInsetsDidChange();
                UIView.setAnimationTransition(UIViewAnimationTransition.None, viewControllerToPresent.view(), true);
                UIView.animateWithDuration(GraphicsBridgeConstants.DefaultAnimationDuration, animation);
                viewControllerToPresent.viewDidAppear(flag);
                if (completion != null)
                    completion.run();
            });
    }

    /**
     * Presents the view that is managed by the specified modal view controller
     * using animation or not according to the parameter.
     *
     * @param modalViewController The view controller of the modal view.
     * @param animated            If TRUE the presentation is animated.
     */
    @Deprecated
    @CMSelector("- (void)presentModalViewController:(UIViewController *)modalViewController animated:(BOOL)animated;")
    public void presentModalViewController(final UIViewController modalViewController, final boolean animated) {
        presentViewController(modalViewController, animated, null);
    }

    /**
     * Dismisses the view controller for this view controller.
     *
     * @param flag       If TRUE the change is animated.
     * @param completion block that runs after the dismissal ends. can be NULL
     */
    @CMSelector("- (void)dismissViewControllerAnimated:(BOOL)flag \n"
            + "                           completion:(void (^)(void))completion;")
    public void dismissViewControllerAnimated(boolean flag, Runnable completion) {
        if (modalViewController != null) {
            modalViewController.view().setTransform(CGAffineTransform.identity());
            Runnable animation = () -> {
                modalViewController.view().setTransform(CGAffineTransform.makeTranslation(0, modalViewController.view().getHeight()));
                modalViewController.view().removeFromSuperview();
            };
            modalViewController.viewWillDisappear(flag);
            UIView.setAnimationTransition(UIViewAnimationTransition.None, UIApplication.sharedApplication().keyWindow().rootViewController().view(), true);
            UIView.animateWithDuration(GraphicsBridgeConstants.DefaultAnimationDuration, animation);
            modalViewController.viewDidDisappear(flag);
            if (completion != null)
                completion.run();
        }
        modalViewController = null;
    }

    /**
     * Dismisses the view controller for this view controller.
     *
     * @param animated If TRUE the change is animated.
     */
    @Deprecated
    @CMSelector("- (void)dismissModalViewControllerAnimated:(BOOL)animated;")
    public void dismissModalViewControllerAnimated(boolean animated) {
        dismissViewControllerAnimated(animated, null);
    }

    /**
     * Returns the size of the pop over view of this view controller.
     *
     * @return The size of the pop over view of this view controller.
     */
    @Deprecated
    @CMGetter("@property(nonatomic, readwrite) CGSize contentSizeForViewInPopover;")
    public CGSize contentSizeForViewInPopover() {
        if (contentSizeForViewInPopover == null)
            contentSizeForViewInPopover = new CGSize(320, 1100);
        return contentSizeForViewInPopover;
    }

    /**
     * Sets the size of the pop over view of this view controller.
     *
     * @param contentSizeForViewInPopover The size of the pop over view of this
     *                                    view controller.
     */
    @Deprecated
    @CMSetter("@property(nonatomic, readwrite) CGSize contentSizeForViewInPopover;")
    public void setContentSizeForViewInPopover(CGSize contentSizeForViewInPopover) {
        this.contentSizeForViewInPopover = contentSizeForViewInPopover;
    }

    /**
     * Returns the edges that should be extended to cover the whole screen.
     *
     * @return The edges that should be extended to cover the whole screen.
     * @see crossmobile.ios.uikit.UIRectEdge
     */
    @CMGetter("@property(nonatomic, assign) UIRectEdge edgesForExtendedLayout;")
    public int edgesForExtendedLayout() {
        return edgesForExtendedLayout;
    }

    /**
     * Sets the edges that should be extended to cover the whole screen.
     *
     * @param UIRectEdge The edges that should be extended to cover the whole
     *                   screen.
     */
    @CMSetter("@property(nonatomic, assign) UIRectEdge edgesForExtendedLayout;")
    public void setEdgesForExtendedLayout(int UIRectEdge) {
        this.edgesForExtendedLayout = UIRectEdge;
    }

    /**
     * Returns a Boolean that shows whether automatic adjustment of scroll views
     * is enabled.
     *
     * @return A Boolean that shows whether automatic adjustment of scroll views
     * is enabled.
     */
    @CMGetter("@property(nonatomic, assign) BOOL automaticallyAdjustsScrollViewInsets;")
    public boolean automaticallyAdjustsScrollViewInsets() {
        return automaticallyAdjustsScrollViewInsets;
    }

    /**
     * Sets a Boolean that defines whether automatic adjustment of scroll views
     * is enabled.
     *
     * @param automaticallyAdjustsScrollViewInsets A Boolean that defines
     *                                             whether automatic adjustment of scroll views is enabled.
     */
    @CMSetter("@property(nonatomic, assign) BOOL automaticallyAdjustsScrollViewInsets;")
    public void setAutomaticallyAdjustsScrollViewInsets(boolean automaticallyAdjustsScrollViewInsets) {
        this.automaticallyAdjustsScrollViewInsets = automaticallyAdjustsScrollViewInsets;
    }

    /**
     * Returns the name of the nib file that is used.
     *
     * @return The name of the nib file that is used.
     */
    @CMGetter("@property(nonatomic, readonly, copy) NSString *nibName;")
    public String nibName() {
        return nibName;
    }

    /**
     * Creates a view for this view controller.
     */
    @CMSelector("- (void)loadView;")
    public void loadView() {
        if (nibName == null)
            view = new UIView();
        else {
            UINib.nibWithNibName(nibName, bundlename).instantiateWithOwner(this, null);
            if (view == null)
                view = new UIView();
        }
    }

    /**
     * Called after the view is loaded into memory.
     */
    @CMSelector("- (void)viewDidLoad")
    public void viewDidLoad() {
    }

    /**
     * Called when the controller is released from memory.
     */
    @Deprecated
    @CMSelector("- (void)viewDidUnload;")
    public void viewDidUnload() {
    }

    /**
     * Called before the view is added to the view controller's hierarchy with
     * animation or not according to the Boolean parameter.
     *
     * @param animated If set TRUE the view is added with animation.
     */
    @CMSelector("- (void)viewWillAppear:(BOOL)animated;")
    public void viewWillAppear(boolean animated) {
    }

    /**
     * Called after the view was added to the view controller's hierarchy with
     * animation or not according to the Boolean parameter.
     *
     * @param animated If set TRUE the view was added with animation.
     */
    @CMSelector("- (void)viewDidAppear:(BOOL)animated;")
    public void viewDidAppear(boolean animated) {
    }

    /**
     * Called when the view is about to be removed from the view controller's
     * hierarchy with animation or not according to the Boolean parameter.
     *
     * @param animated If set TRUE the view is removed with animation.
     */
    @CMSelector("- (void)viewWillDisappear:(BOOL)animated;")
    public void viewWillDisappear(boolean animated) {
    }

    /**
     * Called when the view was removed from the view controller's hierarchy
     * with animation or not according to the Boolean parameter.
     *
     * @param animated If set TRUE the view was removed with animation.
     */
    @CMSelector("- (void)viewDidDisappear:(BOOL)animated;")
    public void viewDidDisappear(boolean animated) {
    }

    /**
     * Called when the controller will layout its subviews.
     */
    @CMSelector("- (void)viewWillLayoutSubviews;")
    public void viewWillLayoutSubviews() {
    }

    /**
     * Called when the controller will layout its subviews.
     */
    @CMSelector("- (void)viewDidLayoutSubviews;")
    public void viewDidLayoutSubviews() {
    }

    /**
     * Returns a Boolean that shows whether the autorotation of the view
     * controller's is enabled.
     *
     * @return A Boolean that shows whether the autorotation of the view
     * controller's is enabled.
     */
    @CMSelector("- (BOOL)shouldAutorotate;")
    public boolean shouldAutorotate() {
        return true;
    }

    /**
     * Returns a Boolean that shows whether the specified orientation is
     * supported.
     *
     * @param UIInterfaceOrientation The orientation that is attempted to be
     *                               applied. The orientation of the app's user interface after the rotation.
     * @return If the specified orientation is supported then TRUE is returned.
     * @see crossmobile.ios.uikit.UIInterfaceOrientation
     */
    @Deprecated
    @CMSelector("- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation;")
    public boolean shouldAutorotateToInterfaceOrientation(int UIInterfaceOrientation) {
        return UIInterfaceOrientation == crossmobile.ios.uikit.UIInterfaceOrientation.Portrait
                || UIInterfaceOrientation == crossmobile.ios.uikit.UIInterfaceOrientation.LandscapeLeft
                || UIInterfaceOrientation == crossmobile.ios.uikit.UIInterfaceOrientation.LandscapeRight
                || (UIInterfaceOrientation == crossmobile.ios.uikit.UIInterfaceOrientation.PortraitUpsideDown && UIDevice.currentDevice().userInterfaceIdiom() == UIUserInterfaceIdiom.Pad);
    }

    /**
     * Returns the supported interface orientations for this view controller.
     *
     * @return The supported interface orientations for this view controller.
     * @see crossmobile.ios.uikit.UIInterfaceOrientationMask
     */
    @CMSelector("- (UIInterfaceOrientationMask)supportedInterfaceOrientations;")
    public int supportedInterfaceOrientations() {
        int mask = 0;
        if (shouldAutorotateToInterfaceOrientation(UIInterfaceOrientation.Portrait))
            mask |= UIInterfaceOrientationMask.Portrait;
        if (shouldAutorotateToInterfaceOrientation(UIInterfaceOrientation.LandscapeRight))
            mask |= UIInterfaceOrientationMask.LandscapeRight;
        if (shouldAutorotateToInterfaceOrientation(UIInterfaceOrientation.PortraitUpsideDown))
            mask |= UIInterfaceOrientationMask.PortraitUpsideDown;
        if (shouldAutorotateToInterfaceOrientation(UIInterfaceOrientation.LandscapeLeft))
            mask |= UIInterfaceOrientationMask.LandscapeLeft;
        if (mask == 0)
            mask = UIInterfaceOrientationMask.Portrait;
        return mask;
    }

    /**
     * Returns the interface orientation of this view controller.
     *
     * @return The interface orientation of this view controller.
     */
    @CMSelector("- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation;")
    public int preferredInterfaceOrientationForPresentation() {
        return crossmobile.ios.uikit.UIInterfaceOrientation.Portrait;
    }

    /**
     * Return a Boolean that shows whether the status bar of this view
     * controller is visible.
     *
     * @return A Boolean that shows whether the status bar of this view
     * controller is visible.
     */
    @CMSelector("- (BOOL)prefersStatusBarHidden;")
    public boolean prefersStatusBarHidden() {
        return false;
    }

    /**
     * Called right before the user interface begins rotating in order to notify
     * the view controller.
     *
     * @param UIInterfaceOrientation The new orientation of the user interface.
     * @param duration               The duration of the rotation expressed in seconds.
     * @see crossmobile.ios.uikit.UIInterfaceOrientation
     */
    //@Deprecated
    @CMSelector("- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration;")
    @Deprecated
    public void willRotateToInterfaceOrientation(int UIInterfaceOrientation, double duration) {
    }

    /**
     * Called right before the user interface begins one-step user rotating in
     * order to notify the view controller.
     *
     * @param UIInterfaceOrientation The new orientation of the user interface.
     * @param duration               The duration of the rotation expressed in seconds.
     * @see crossmobile.ios.uikit.UIInterfaceOrientation
     */
    @Deprecated
    @CMSelector("- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration;")
    public void willAnimateRotationToInterfaceOrientation(int UIInterfaceOrientation, double duration) {
    }

    /**
     * Called after the user interface rotation in order to notify the view
     * controller.
     *
     * @param UIInterfaceOrientation The previous orientation of the user
     *                               interface.
     * @see crossmobile.ios.uikit.UIInterfaceOrientation
     */
    @Deprecated
    @CMSelector("- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation;")
    public void didRotateFromInterfaceOrientation(int UIInterfaceOrientation) {
    }

    /**
     * Called when a memory warning arises in order to notify the view
     * controller.
     */
    @CMSelector("- (void)didReceiveMemoryWarning")
    public void didReceiveMemoryWarning() {
    }

    UIScrollView getFirstScroll() {
        if (!scrollWasAlreadySearched && view != null) {
            scrollWasAlreadySearched = true;
            UIView fs = view.subview(UIScrollView.class, UIWebView.class);
            firstScroll = (fs instanceof UIWebView) ? ((UIWebView) fs).scrollView() : (UIScrollView) fs;
        }
        return firstScroll;
    }

    CGRect getActiveFrame() {
        CGRect frame = view().frame();
        UIEdgeInsets metrics = getActiveInsets();
        frame.getOrigin().setX(metrics.getLeft());
        frame.getOrigin().setY(metrics.getTop());
        frame.getSize().setWidth(frame.getSize().getWidth() - (metrics.getLeft() + metrics.getRight()));
        frame.getSize().setHeight(frame.getSize().getHeight() - (metrics.getBottom() + metrics.getTop()));
        return frame;
    }

    UIEdgeInsets getTotalSafeAreaInsets() {
        UIEdgeInsets metrics = getActiveInsets();
        return new UIEdgeInsets(metrics.getTop() + additionalSafeAreaInsets.getTop(), metrics.getLeft() + additionalSafeAreaInsets.getLeft(),
                metrics.getBottom() + additionalSafeAreaInsets.getBottom(), metrics.getRight() + additionalSafeAreaInsets.getRight());
    }

    UIEdgeInsets getActiveInsets() {
        if (UIApplication.sharedApplication() == null || UIApplication.sharedApplication().keyWindow() == null || UIApplication.sharedApplication().keyWindow().rootViewController() != this)
            return UIEdgeInsets.zero();
        DrawableMetrics metrics = Native.graphics().metrics();
        return new UIEdgeInsets(metrics.getInsetTop(), metrics.getInsetLeft(),
                metrics.getInsetBottom(), metrics.getInsetRight());
    }

    boolean isView(UIView parent) {
        return (view != null && parent != null && view.equals(parent));
    }

    @CMGetter("@property(nonatomic, readonly, strong) NSExtensionContext *extensionContext;")
    public NSExtensionContext extensionContext() {
        return extensionContext;
    }

    @CMGetter("@property(nonatomic) UIEdgeInsets additionalSafeAreaInsets;")
    public UIEdgeInsets additionalSafeAreaInsets() {
        return additionalSafeAreaInsets;
    }

    @CMSetter("@property(nonatomic) UIEdgeInsets additionalSafeAreaInsets;")
    public void setAdditionalSafeAreaInsets(UIEdgeInsets additionalSafeAreaInsets) {
        this.additionalSafeAreaInsets = additionalSafeAreaInsets;
        if (view != null)
            view.safeAreaInsets();
    }

    @CMSelector("- (void)viewSafeAreaInsetsDidChange;")
    public void viewSafeAreaInsetsDidChange() {

    }

    @CMGetter("@property(nonatomic, readonly, strong) id<UILayoutSupport> topLayoutGuide;")
    public UILayoutSupport topLayoutGuide() {
        if (topLayoutGuide == null)
            topLayoutGuide = new LayoutSupport(view, true);
        return topLayoutGuide;
    }

    @CMGetter("@property(nonatomic, readonly, strong) id<UILayoutSupport> bottomLayoutGuide;")
    public UILayoutSupport bottomLayoutGuide() {
        if (bottomLayoutGuide == null)
            bottomLayoutGuide = new LayoutSupport(view, false);
        return bottomLayoutGuide;
    }

    void suggestGuides(UIEdgeInsets insets) {
        if (topLayoutGuide != null) {
            CGSize current = ((LayoutSupport) topLayoutGuide).owningView().frame().getSize();
            ((LayoutSupport) topLayoutGuide).owningView().solver().suggestValue(((LayoutSupport) topLayoutGuide).getVariable(NSLayoutAttribute.Top), view().lookup(NSLayoutAttribute.Top, current.getWidth(), current.getHeight()));
            ((LayoutSupport) topLayoutGuide).owningView().solver().suggestValue(((LayoutSupport) topLayoutGuide).getVariable(NSLayoutAttribute.Height), insets.getTop());
            ((LayoutSupport) topLayoutGuide).owningView().solver().resolve();
        }
        if (bottomLayoutGuide != null) {
            CGSize current = ((LayoutSupport) bottomLayoutGuide).owningView().frame().getSize();
            ((LayoutSupport) bottomLayoutGuide).owningView().solver().suggestValue(((LayoutSupport) bottomLayoutGuide).getVariable(NSLayoutAttribute.Top), view().lookup(NSLayoutAttribute.Height, current.getWidth(), current.getHeight()) - insets.getBottom());
            ((LayoutSupport) bottomLayoutGuide).owningView().solver().suggestValue(((LayoutSupport) bottomLayoutGuide).getVariable(NSLayoutAttribute.Height), insets.getBottom());
            ((LayoutSupport) bottomLayoutGuide).owningView().solver().resolve();
        }
    }

    private void setLayoutGuide(LayoutSupport guide) {
        guide.owningView().solver().beginEdit();
        for (Integer attr : guide.variableMap.keySet())
            guide.owningView().solver().addEditVar(guide.getVariable(attr));
        guide.owningView().solver().resolve();
    }

    private void resetLayoutGuide(LayoutSupport guide) {
        if (guide.owningView() == null)
            return;
        guide.owningView().solver().beginEdit();
        for (Integer attr : guide.variableMap.keySet())
            if (guide.owningView().solver().containsVariable(guide.getVariable(attr)))
                guide.owningView().solver().removeEditVar(guide.getVariable(attr));
        guide.owningView().solver().resolve();
    }

    /**
     * This should be called whenever a view is set
     * <p>
     * Behaviour when UIViewControllers view is set as null by force cannot be tested
     * on linux.
     */
    private void resetLayoutSupport() {
        if (topLayoutGuide != null) {
            resetLayoutGuide((LayoutSupport) topLayoutGuide);
            ((LayoutSupport) topLayoutGuide).owningView = view;
            setLayoutGuide((LayoutSupport) topLayoutGuide);
        }
        if (bottomLayoutGuide != null) {
            resetLayoutGuide((LayoutSupport) bottomLayoutGuide);
            ((LayoutSupport) bottomLayoutGuide).owningView = view;
            setLayoutGuide((LayoutSupport) bottomLayoutGuide);
        }
    }

    //Managing Child View Controllers in a Custom Container

    @CMGetter("@property(nonatomic, readonly) NSArray<__kindof UIViewController *> *childViewControllers;")
    public List<UIViewController> childViewControllers() {
        return childViewControllers;
    }

    @CMSelector("- (void)addChildViewController:(UIViewController *)childController;")
    public void addChildViewController(UIViewController childController) {
        if (childController.parentViewController() != null)
            childController.removeFromParentViewController();
        childViewControllers.add(childController);
    }

    @CMSelector("- (void)removeFromParentViewController;")
    public void removeFromParentViewController() {
        pcontroller.childViewControllers.remove(this);
    }

    @CMSelector("- (void)transitionFromViewController:(UIViewController *)fromViewController \n" +
            "                    toViewController:(UIViewController *)toViewController \n" +
            "                            duration:(NSTimeInterval)duration \n" +
            "                             options:(UIViewAnimationOptions)options \n" +
            "                          animations:(void (^)(void))animations \n" +
            "                          completion:(void (^)(BOOL finished))completion;")
    public void transitionFromViewController(UIViewController fromViewController, UIViewController toViewController, double duration, int options, Runnable animations, VoidBlock1<Boolean> completion) {
        if (fromViewController.pcontroller == null || toViewController.pcontroller == null || !toViewController.pcontroller.equals(fromViewController.pcontroller)) {
            Native.system().error("'Children view controllers " + fromViewController + " and " + toViewController + " must have a common parent view controller when calling transitionFromViewController(...)'", new RuntimeException());
        }
        fromViewController.beginAppearanceTransition(false, duration > 0);
        toViewController.beginAppearanceTransition(true, duration > 0);
        fromViewController.pcontroller.view.addSubview(toViewController.view());
        if (duration == 0) {
            fromViewController.view.removeFromSuperview();
            completion.invoke(true);
        } else
            UIView.animateWithDuration(duration, 0, options, animations::run, input -> {
                if (input) {
                    fromViewController.view.removeFromSuperview();
                    completion.invoke(input);
                    fromViewController.endAppearanceTransition();
                    toViewController.endAppearanceTransition();
                }
            });
    }

    @CMGetter("@property(nonatomic, readonly) BOOL shouldAutomaticallyForwardAppearanceMethods;")
    public boolean shouldAutomaticallyForwardAppearanceMethods() {
        return true;
    }

    @CMSelector("- (void)beginAppearanceTransition:(BOOL)isAppearing \n" +
            "                         animated:(BOOL)animated;")
    public void beginAppearanceTransition(boolean isAppearing, boolean animated) {

    }

    @CMSelector("- (void)endAppearanceTransition;")
    public void endAppearanceTransition() {

    }

    //Getting Other Related View Controllers
    @CMGetter("@property(nonatomic, readonly) UIViewController *presentingViewController;")
    public UIViewController presentingViewController() {
        if (presentingViewController == null && parentViewController() != null) {
            presentingViewController = parentViewController().presentingViewController();
        }
        return presentingViewController;
    }

    @CMGetter("@property(nonatomic, readonly) UIViewController *presentedViewController;")
    public UIViewController presentedViewController() {
        return modalViewController;
    }

    /**
     * Returns the parent view of this view controller.
     *
     * @return The parent view of this view controller.
     */
    @CMGetter("@property(nonatomic, weak, readonly) UIViewController *parentViewController;")
    public UIViewController parentViewController() {
        return pcontroller;
    }

    void setParentController(UIViewController parentController) {
        pcontroller = parentController;
        if (parentController != null && parentController instanceof UINavigationController)
            navigationItem(); // Initialize navigation item
    }


    /**
     * Returns the nearest ancestor in hierarchy, that is a navigation
     * controller.
     *
     * @return The nearest ancestor that is a navigation controller.
     */
    @CMGetter("@property(nonatomic, readonly, strong) UINavigationController *navigationController;")
    public UINavigationController navigationController() {
        return getAncestorOf(UINavigationController.class);
    }

    /**
     * Returns the nearest ancestor in the hierarchy that is a split view
     * controller.
     *
     * @return The nearest ancestor in the hierarchy that is a split view
     * controller.
     */
    @CMGetter("@property(nonatomic, readonly, strong) UISplitViewController *splitViewController;")
    public UISplitViewController splitViewController() {
        return getAncestorOf(UISplitViewController.class);
    }

    /**
     * Returns the nearest ancestor in the hierarchy that is a Tab bar
     * controller.
     *
     * @return The nearest ancestor in the hierarchy that is a Tab bar
     * controller.
     */
    @CMGetter("@property(nonatomic, readonly, strong) UITabBarController *tabBarController;")
    public UITabBarController tabBarController() {
        return getAncestorOf(UITabBarController.class);
    }

    private <T extends UIViewController> T getAncestorOf(Class<T> clazz) {
        UIViewController p = pcontroller;
        while (p != null) {
            if (clazz.isInstance(p))
                //noinspection unchecked
                return (T) p;
            p = p.pcontroller;
        }
        return null;
    }

    boolean viewShouldNotOverlapWithParentDecorations() {
        return false;
    }

    @CMGetter("@property(nonatomic, copy) NSString *restorationIdentifier;")
    public String restorationIdentifier() {
        return restorationIdentifier;
    }

    @CMSetter("@property(nonatomic, copy) NSString *restorationIdentifier;")
    public void setRestorationIdentifier(String restorationIdentifier) {
        //TODO what it actually does
        this.restorationIdentifier = restorationIdentifier;
    }

    class LayoutSupport implements UILayoutSupport {

        private final NSLayoutYAxisAnchor topAnchor = new NSLayoutYAxisAnchor(this, NSLayoutAttribute.Top);
        private final NSLayoutYAxisAnchor bottomAnchor = new NSLayoutYAxisAnchor(this, NSLayoutAttribute.Bottom);
        private final NSLayoutDimension heightAnchor = new NSLayoutDimension(this, NSLayoutAttribute.Height);
        private UIView owningView;
        private final boolean top;

        Map<Integer, ClVariable> variableMap = new HashMap<>();
        private CGRect layoutFrame = CGRect.zero();

        LayoutSupport(UIView owningView, boolean top) {
            this.top = top;
            this.owningView = owningView;
            initVars();
        }

        UIView owningView() {
            return owningView;
        }

        @Override
        public double length() {
            return top ? owningView.safeAreaInsets().getTop() : owningView.safeAreaInsets().getBottom();
        }

        @Override
        public NSLayoutYAxisAnchor bottomAnchor() {
            return bottomAnchor;
        }

        @Override
        public NSLayoutDimension heightAnchor() {
            return heightAnchor;
        }

        @Override
        public NSLayoutYAxisAnchor topAnchor() {
            return topAnchor;
        }

        ClVariable getVariable(int attribute) {
            return variableMap.get(attribute);
        }

        private void initVars() {
            variableMap.put(NSLayoutAttribute.Left, new ClVariable(this.getClass().getSimpleName() + "@" + this.hashCode() + "_Left"));
            variableMap.put(NSLayoutAttribute.Top, new ClVariable(this.getClass().getSimpleName() + "@" + this.hashCode() + "_Top"));
            variableMap.put(NSLayoutAttribute.Width, new ClVariable(this.getClass().getSimpleName() + "@" + this.hashCode() + "_Width"));
            variableMap.put(NSLayoutAttribute.Height, new ClVariable(this.getClass().getSimpleName() + "@" + this.hashCode() + "_Height"));
        }

        void applyResult() {
            layoutFrame.getSize().setWidth((float) getVariable(NSLayoutAttribute.Width).getValue());
            layoutFrame.getSize().setHeight((float) getVariable(NSLayoutAttribute.Height).getValue());
            layoutFrame.getOrigin().setX((float) getVariable(NSLayoutAttribute.Left).getValue() - owningView().getX());
            layoutFrame.getOrigin().setY((float) getVariable(NSLayoutAttribute.Top).getValue() - owningView().getY());
        }
    }
}
