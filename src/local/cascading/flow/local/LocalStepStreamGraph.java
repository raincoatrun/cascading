/*
 * Copyright (c) 2007-2012 Concurrent, Inc. All Rights Reserved.
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

package cascading.flow.local;

import java.util.List;
import java.util.Properties;

import cascading.flow.FlowElement;
import cascading.flow.FlowProcess;
import cascading.flow.stream.Duct;
import cascading.flow.stream.Gate;
import cascading.flow.stream.MemoryCoGroupGate;
import cascading.flow.stream.SourceStage;
import cascading.flow.stream.StepStreamGraph;
import cascading.pipe.CoGroup;
import cascading.pipe.GroupBy;
import cascading.pipe.Merge;
import cascading.pipe.Splice;
import cascading.tap.Tap;

/**
 *
 */
public class LocalStepStreamGraph extends StepStreamGraph
  {

  public LocalStepStreamGraph( FlowProcess<Properties> flowProcess, LocalFlowStep step )
    {
    super( flowProcess, step );

    buildGraph();
    setTraps();
    setScopes();

    printGraph( step.getID(), "local", 0 );

    bind();
    }

  protected void buildGraph()
    {
    for( Object rhsElement : step.getSources() )
      {
      Duct rhsDuct = new SourceStage( flowProcess, (Tap) rhsElement );

      addHead( rhsDuct );

      handleDuct( (FlowElement) rhsElement, rhsDuct );
      }
    }

  protected Gate createCoGroupGate( CoGroup element )
    {
    return new MemoryCoGroupGate( flowProcess, (Splice) element );
    }

  protected Gate createGroupByGate( GroupBy element )
    {
    return new LocalGroupByGate( flowProcess, (Splice) element );
    }

  @Override
  protected Duct createMergeStage( Merge merge )
    {
    return new SyncMergeStage( flowProcess, merge );
    }


  protected boolean stopOnElement( FlowElement lhsElement, List<FlowElement> successors )
    {
    if( successors.isEmpty() )
      {
      if( !( lhsElement instanceof Tap ) )
        throw new IllegalStateException( "expected a Tap instance" );

      return true;
      }

    return false;
    }
  }
