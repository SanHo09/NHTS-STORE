package com.nhom4.nhtsstore.ui.page.user;

import com.nhom4.nhtsstore.entities.rbac.User;
import com.nhom4.nhtsstore.services.IUserService;
import com.nhom4.nhtsstore.ui.base.GenericTablePanel;
import com.nhom4.nhtsstore.ui.navigation.RoutablePanel;
import com.nhom4.nhtsstore.ui.navigation.RouteParams;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
@Scope("prototype")
public class UserListPanel extends GenericTablePanel<User> implements RoutablePanel {
	private static final String[] USER_COLUMNS = {
			"Id", "Full name", "Username","Role", "Status", "Updated At ↓", "Updated By"
	};
	private static final List<String> SEARCH_FIELDS = Arrays.asList("username", "fullName", "email");
	private static final String placeHolderMessage = "Search by Username/Full name/Email";
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor cho GenericTablePanel
	 *
	 * @param service            Service cung cấp dữ liệu và thao tác với database

	 */
	public UserListPanel(IUserService service) {
		super(service, User.class, UserProfilePanel.class, UserCreatePanel.class,null, USER_COLUMNS, "Users", SEARCH_FIELDS, placeHolderMessage);
	}

	@Override
	public void onNavigate(RouteParams params) {

	}
}
