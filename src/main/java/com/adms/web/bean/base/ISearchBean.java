package com.adms.web.bean.base;

import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface ISearchBean<T> {

	public List<T> search(final T object, final PageRequest pageRequest);
	public Long getTotalCount(final T object);
}
