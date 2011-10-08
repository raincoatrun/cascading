/*
 * Copyright (c) 2007-2011 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.util;

import java.util.Map;
import java.util.Properties;

import cascading.CascadingException;

/**
 *
 */
public class ServiceUtil
  {

  // look in meta-inf/cascading-services for all classnames

  public static Map<String, String> findAllServices()
    {
    return null;
    }

  public static CascadingService loadServiceFrom( Properties defaultProperties, Map<Object, Object> properties, String property )
    {
    String className = PropertyUtil.getProperty( properties, property, defaultProperties.getProperty( property ) );

    return createService( properties, className );
    }

  public static CascadingService createService( Map<Object, Object> properties, String className )
    {
    if( className == null || className.isEmpty() )
      return null;

    try
      {
      Class type = Thread.currentThread().getContextClassLoader().loadClass( className );

      CascadingService service = (CascadingService) type.newInstance();

      service.setProperties( properties );

      return service;
      }
    catch( ClassNotFoundException exception )
      {
      throw new CascadingException( "unable to find service class: " + className, exception );
      }
    catch( IllegalAccessException exception )
      {
      throw new CascadingException( "unable to instantiate service class: " + className, exception );
      }
    catch( InstantiationException exception )
      {
      throw new CascadingException( "unable to instantiate service class: " + className, exception );
      }

    }
  }