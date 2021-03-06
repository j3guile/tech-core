/**
 * 
 */
package com.appirio.tech.sample.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;
import com.appirio.tech.core.api.v2.model.annotation.ApiMapping;
import com.appirio.tech.core.api.v2.request.FieldSelector;
import com.appirio.tech.core.api.v2.request.FilterParameter;
import com.appirio.tech.core.api.v2.request.QueryParameter;
import com.appirio.tech.core.api.v2.request.SortOrder;
import com.appirio.tech.core.api.v2.service.RESTPersistentService;
import com.appirio.tech.sample.exception.StorageException;
import com.appirio.tech.sample.model.User;
import com.appirio.tech.sample.storage.InMemoryStorage;

/**
 * @author sudo
 *
 */
@Service
public class UserCRUDService implements RESTPersistentService<User> {

	private InMemoryStorage storage = InMemoryStorage.instance();
	
	@ApiMapping(visible = false)
	public String getResourcePath() {
		return User.RESOURCE_PATH;
	}

	public AbstractIdResource handleGet(FieldSelector selector, CMCID recordId) throws Exception {
		for(User user : storage.getUserList()) {
			if(user.getId().equals(recordId)) {
				return user;
			}
		}
		return null;
	}

	public List<User> handleGet(HttpServletRequest request, QueryParameter query) throws Exception {
		FilterParameter parameter = query.getFilter();
		
		List<User> resultList = new ArrayList<User>();
		
		//Filter to specified queries
		for(User user : storage.getUserList()) {
			if(parameter.contains("handle") && !(parameter.get("handle").equals(user.getHandle()))) continue;
			if(parameter.contains("email") && !(parameter.get("email").equals(user.getEmail()))) continue;
			if(parameter.contains("firstName") && !(parameter.get("firstName").equals(user.getFirstName()))) continue;
			if(parameter.contains("lastName") && !(parameter.get("lastName").equals(user.getLastName()))) continue;
			resultList.add(user);
		}
		
		//order_by
		if(query.getOrderByQuery().getOrderByField()!=null) {
			String orderField = query.getOrderByQuery().getOrderByField();
			final Boolean order = SortOrder.ASC_NULLS_FIRST==query.getOrderByQuery().getSortOrder()?true:false;
			if(orderField.equals("handle")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getHandle().compareTo(o2.getHandle());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("email")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getEmail().compareTo(o2.getEmail());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("firstName")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getFirstName().compareTo(o2.getFirstName());
						return order?result:-1*result;
					}
				});
			}else if(orderField.equals("lastName")) {
				Collections.sort(resultList, new Comparator<User>() {
					public int compare(User o1, User o2) {
						int result = o1.getLastName().compareTo(o2.getLastName());
						return order?result:-1*result;
					}
				});
			}else {
				throw new StorageException("Unknown field on order by :" + orderField);
			}
		}
		
		//limit, off set
		if(query.getLimitQuery().getOffset() != null) {
			resultList = resultList.subList(query.getLimitQuery().getOffset(), resultList.size());
		}
		if(query.getLimitQuery().getLimit() != null && query.getLimitQuery().getLimit()<resultList.size()) {
			resultList = resultList.subList(0, query.getLimitQuery().getLimit());
		}
		
		return resultList;
	}

	public CMCID handlePost(HttpServletRequest request, User object) throws Exception {
		return storage.insertUser(object).getId();
	}

	public CMCID handlePut(HttpServletRequest request, User object) throws Exception {
		CMCID id = object.getId();
		User orgUser = null;
		for(User user : storage.getUserList()) {
			if(user.getId().equals(id)) {
				orgUser = user; break;
			}
		}
		if(orgUser==null) {
			throw new StorageException("Id of the User not found:" + id);
		}
		orgUser.setHandle(object.getHandle());
		orgUser.setEmail(object.getEmail());
		orgUser.setFirstName(object.getFirstName());
		orgUser.setLastName(object.getLastName());
		return id;
	}

	public void handleDelete(HttpServletRequest request, CMCID id) throws Exception {
		storage.deleteUser(id);
	}

	/**
	 * We're not going to use this method.
	 */
	public DaoBase<User> getResourceDao() {
		return null;
	}

}
