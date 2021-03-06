package com.appirio.tech.core.api.v2.service;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v2.CMCID;
import com.appirio.tech.core.api.v2.dao.DaoBase;
import com.appirio.tech.core.api.v2.model.AbstractIdResource;

/**
 * Interface to handle REST calls for persistent resources.
 * 
 * @author sudo
 *
 */
public interface RESTPersistentService<T extends AbstractIdResource> extends RESTQueryService<T> {

	CMCID handlePost(HttpServletRequest request, T object) throws Exception;

	CMCID handlePut(HttpServletRequest request, T object) throws Exception;
	
	void handleDelete(HttpServletRequest request, CMCID id) throws Exception;

	DaoBase<T> getResourceDao();
}
