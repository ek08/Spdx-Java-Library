package org.spdx.storage.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.library.model.TypedValue;
import org.spdx.storage.IModelStore;

/**
 * @author gary
 *
 */
/**
 * @author gary
 *
 */
class StoredTypedItem extends TypedValue {
	
	static Set<String> SPDX_CLASSES = new HashSet<>(Arrays.asList(SpdxConstants.ALL_SPDX_CLASSES));

	private ConcurrentHashMap<String, Object> properties = new ConcurrentHashMap<>();
	
	public StoredTypedItem(String documentUri, String id, String type) throws InvalidSPDXAnalysisException {
		super(documentUri, id, type);
	}
	
	/**
	 * @returnProperty names for all properties having a value
	 */
	public List<String> getPropertyValueNames() {
		Iterator<Entry<String, Object>> iter = this.properties.entrySet().iterator();
		List<String> retval = new ArrayList<>();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			if (!(entry.getValue() instanceof List)) {
				retval.add(entry.getKey());
			}
		}
		return retval;
	}
	
	/**
	 * @return Property names for all properties have a value list
	 */
	public List<String> getPropertyValueListNames() {
		Iterator<Entry<String, Object>> iter = this.properties.entrySet().iterator();
		List<String> retval = new ArrayList<>();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			if (entry.getValue() instanceof List) {
				retval.add(entry.getKey());
			}
		}
		return retval;
	}

	
	/**
	 * @param propertyName Name of the property
	 * @param value Value to be set
	 */
	public void setValue(String propertyName, Object value) {
		properties.put(propertyName, value);
	}
	
	/**
	 * Sets the value list for the property to an empty list creating the propertyName if it does not exist
	 * @param propertyName Name of the property
	 * @throws SpdxInvalidTypeException
	 */
	public void clearPropertyValueList(String propertyName) throws SpdxInvalidTypeException {
		Object value = properties.getOrDefault(propertyName, new ArrayList<Object>());
		if (value == null) {
			throw new SpdxInvalidTypeException("No list for list property value for property "+propertyName);
		}
		if (!(value instanceof List)) {
			throw new SpdxInvalidTypeException("Trying to clear a list for non list type for property "+propertyName);
		}
		((List<?>)value).clear();
	}

	/**
	 * Adds a value to a property list for a String or Boolean type of value creating the propertyName if it does not exist
	 * @param propertyName Name of the property
	 * @param value Value to be set
	 * @throws SpdxInvalidTypeException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addValueToList(String propertyName, Object value) throws SpdxInvalidTypeException {
		Object list = properties.get(propertyName);
		if (list == null) {
			properties.putIfAbsent(propertyName,  new ArrayList<Object>());
			list = properties.get(propertyName);	
			//Note: there is a small timing window where the property could be removed
			if (list == null) {
				return;
			}
		}
		if (!(list instanceof List)) {
			throw new SpdxInvalidTypeException("Trying to add a list for non list type for property "+propertyName);
		}
		try {
			((List)list).add(value);
		} catch (Exception ex) {
			throw new SpdxInvalidTypeException("Invalid list type for "+propertyName);
		}
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return List of values associated with the id, propertyName and document
	 * @throws SpdxInvalidTypeException
	 */
	public List<?> getValueList(String propertyName) throws SpdxInvalidTypeException {
		Object list = properties.get(propertyName);
		if (list == null) {
			return null;
		}
		if (!(list instanceof List)) {
			throw new SpdxInvalidTypeException("Trying to get a list for non list type for property "+propertyName);
		}
		if (!(list instanceof List<?>)) {
			throw new SpdxInvalidTypeException("Invalid list type for "+propertyName);
		}
		return (List<?>)list;
	}
	
	/**
	 * @param propertyName Name of the property
	 * @return the single value associated with the id, propertyName and document
	 */
	public Object getValue(String propertyName) {
		return properties.get(propertyName);
	}
	
	/**
	 * Removes a property from the document for the given ID if the property exists.  Does not raise any exception if the propertyName does not exist
	 * @param propertyName Name of the property
	 */
	public void removeProperty(String propertyName) {
		properties.remove(propertyName);
	}

	/**
	 * Copy all values for this item from another store
	 * @param store
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void copyValuesFrom(IModelStore store) throws InvalidSPDXAnalysisException {
		List<String> propertyNames = store.getPropertyValueNames(this.getDocumentUri(), this.getId());
		for (String propertyName:propertyNames) {
			this.addValueToList(propertyName, store.getValue(getDocumentUri(), getId(), propertyName));
		}
		List<String> propertyListNames = store.getPropertyValueListNames(getDocumentUri(), getId());
		for (String propertyListName:propertyListNames) {
			List<?> list = store.getValueList(getDocumentUri(), getId(), propertyListName);
			for (Object listItem:list) {
				addValueToList(propertyListName, listItem);
			}
		}
	}
}