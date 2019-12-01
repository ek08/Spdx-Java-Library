/**
 * Copyright (c) 2015, 2019 Source Auditor Inc.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package org.spdx.library.model.license;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxInvalidTypeException;
import org.spdx.storage.IModelStore;


/**
 * The SimpleLicenseInfo class includes all resources that represent 
 * simple, atomic, licensing information.
 * 
 * @author Gary O'Neall
 *
 */
public abstract class SimpleLicensingInfo extends AnyLicenseInfo {
	
	/**
	 * Open or create a model object with the default store and default document URI
	 * @param id ID for this object - must be unique within the SPDX document
	 * @throws InvalidSPDXAnalysisException 
	 */
	SimpleLicensingInfo(String id) throws InvalidSPDXAnalysisException {
		super(id);
	}

	/**
	 * Create a new SimpleLicensingInfo object
	 * @param modelStore container which includes the license
	 * @param documentUri URI for the SPDX document containing the license
	 * @param id identifier for the license
	 * @param create if true, create the license if it does not exist
	 * @throws InvalidSPDXAnalysisException 
	 */
	SimpleLicensingInfo(IModelStore modelStore, String documentUri, String id, boolean create) throws InvalidSPDXAnalysisException {
		super(modelStore, documentUri, id, create);
	}
	
	/**
	 * @return the id
	 */
	public String getLicenseId() {
		return this.getId();
	}

	/**
	 * @return the name
	 * @throws SpdxInvalidTypeException 
	 */
	public String getName() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(PROP_STD_LICENSE_NAME);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param name the name to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setName(String name) throws InvalidSPDXAnalysisException {
		setPropertyValue(PROP_STD_LICENSE_NAME, name);
	}
	
	/**
	 * @return the comments
	 * @throws SpdxInvalidTypeException 
	 */
	public String getComment() throws InvalidSPDXAnalysisException {
		Optional<String> o = getStringPropertyValue(RDFS_PROP_COMMENT);
		if (o.isPresent()) {
			return o.get();
		} else {
			return "";
		}
	}
	
	/**
	 * @param comment the comment to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setComment(String comment) throws InvalidSPDXAnalysisException {
		setPropertyValue(RDFS_PROP_COMMENT, comment);
	}
	
	/**
	 * @return the urls which reference the same license information
	 * @throws SpdxInvalidTypeException 
	 */
	public Collection<String> getSeeAlso() throws InvalidSPDXAnalysisException {
		return getStringCollection(RDFS_PROP_SEE_ALSO);
	}
	/**
	 * @param seeAlsoUrl the urls which are references to the same license to set
	 * @throws InvalidSPDXAnalysisException 
	 */
	public void setSeeAlso(List<String> seeAlsoUrl) throws InvalidSPDXAnalysisException {
		if (seeAlsoUrl == null) {
			clearValueCollection(RDFS_PROP_SEE_ALSO);
		} else {
			setPropertyValue(RDFS_PROP_SEE_ALSO, seeAlsoUrl);
		}
	}
}
