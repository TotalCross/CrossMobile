/*
 * (c) 2019 by Panayotis Katsaloulis
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the CrossMobile Community License as published
 * by the CrossMobile team.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * CrossMobile Community License for more details.
 *
 * You should have received a copy of the CrossMobile Community
 * License along with CrossMobile; if not, please contact the
 * CrossMobile team at https://crossmobile.tech/contact/
 */
package org.crossmobile.plugin.model;

import org.crossmobile.plugin.reg.ObjectRegistry;
import org.crossmobile.plugin.reg.TypeDef;
import org.crossmobile.plugin.reg.TypeRegistry;
import org.crossmobile.utils.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import static org.crossmobile.plugin.utils.Texters.toObjC;
import static org.crossmobile.utils.NamingUtils.getClassNameBare;

public final class NObject {

    private final Collection<NObject> parentProtocols = new HashSet<>();
    private final Collection<NSelector> selectors = new TreeSet<>();
    private final String family;
    private final Class type;
    private final Collection<NStructField> structFields;
    private String code;

    public NObject(String family, Class type) {
        this.family = family;
        this.type = type;
        structFields = NStructField.find(type);
    }

    public Class getType() {
        return type;
    }

    public String getFamily() {
        return family;
    }

    public void addSelector(NSelector selector) {
        if (selector == null)
            return;
        selectors.add(selector);
        selector.setContainer(this);
    }

    public Collection<NSelector> getSelectors() {
        return selectors;
    }

    public Collection<NSelector> getAllSelectors() {
        Collection<NSelector> allSelectors = new TreeSet<>();
        getSelectorsRecursively(this, allSelectors, true);
        return allSelectors;
    }

    private static void getSelectorsRecursively(NObject base, Collection<NSelector> selectors, boolean thisObject) {
        if (base == null) {
        } else if (!base.isObjCBased())
            selectors.addAll(base.selectors);
        else {
            getSelectorsRecursively(ObjectRegistry.retrieve(base.type.getSuperclass()), selectors, false);
            for (NSelector sel : base.selectors)
                if (thisObject || sel.needsOverrideBindings())
                    selectors.add(sel);
        }
    }

    public Collection<String> getDependencies(boolean allSelectors) {
        Collection<String> dependencies = new TreeSet<>();
        for (NSelector selector : allSelectors ? getAllSelectors() : getSelectors())
            dependencies.addAll(selector.getDependencies());
        if (isStruct())
            for (Field f : type.getFields())
                if (!f.getType().isPrimitive())
                    dependencies.add(toObjC(getClassNameBare(f.getType())));
        if (isCBased())
            dependencies.add(toObjC(getClassNameBare(Object.class)));
        dependencies.remove(toObjC(getClassNameBare(type)));
        if (CollectionUtils.containsAny(dependencies, TypeDef.Numbers))
            dependencies.add("java_lang_Number");
        return dependencies;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Collection<NObject> getParentProtocols() {
        return parentProtocols;
    }

    public boolean isCBased() {
        return TypeRegistry.isCBased(type);
    }

    public boolean isObjCBased() {
        return TypeRegistry.isObjCBased(type);
    }

    public boolean isAnyReference() {
        return TypeRegistry.isAnyReference(type);
    }

    public boolean isBundle() {
        return TypeRegistry.isBundle(type);
    }

    public boolean isStruct() {
        return TypeRegistry.isStruct(type);
    }

    public boolean isReference() {
        return TypeRegistry.isReference(type);
    }

    public boolean isProtocol() {
        return TypeRegistry.isProtocol(type);
    }

    public boolean isTarget() {
        return TypeRegistry.isTarget(type);
    }

    public boolean needsOverrideBindings() {
        return !Modifier.isFinal(type.getModifiers()) && isObjCBased();
    }

    public Collection<NStructField> getStructFields() {
        return structFields;
    }
}
