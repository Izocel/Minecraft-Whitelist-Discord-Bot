import "./App.css";
import * as go from "gojs";
import { useEffect, useState } from "react";
import { DiagramWrapper } from "./wrappers/DiagramWrapper";

interface AppState {
  nodeDataArray: Array<go.ObjectData>;
  linkDataArray: Array<go.ObjectData>;
  modelData: go.ObjectData;
  selectedKey: number | null;
  skipsDiagramUpdate: boolean;
}

function App() {
  console.clear();
  const [isLoaded, setIsLoaded] = useState(true);
  const [modelData, setModelData] = useState({ canRelink: true });
  const [skipsDiagramUpdate, setSkipsDiagramUpdate] = useState(false);
  const [diagramRef, setDiagramRef] = useState<DiagramWrapper | null>(null);
  const [selectedNodeData, setSelectedNodeData] = useState<go.ObjectData>({});

  const [nodeDataArray, setNodeDataArray] = useState([
    { key: 0, text: "Alpha", color: "lightblue", loc: "0 0" },
    { key: 1, text: "Beta", color: "orange", loc: "150 0" },
    { key: 2, text: "Gamma", color: "lightgreen", loc: "0 150" },
    { key: 3, text: "Delta", color: "pink", loc: "150 150" },
  ]);

  const [linkDataArray, setLinkDataArray] = useState([
    { key: -1, from: 0, to: 1 },
    { key: -2, from: 0, to: 2 },
    { key: -3, from: 1, to: 1 },
    { key: -4, from: 2, to: 3 },
    { key: -5, from: 3, to: 0 },
  ]);

  /**
   * Handle any app-specific DiagramEvents, in this case just selection changes.
   * On ChangedSelection, find the corresponding data and set the selectedKey state.
   *
   * This is not required, and is only needed when handling DiagramEvents from the GoJS diagram.
   * @param e a GoJS DiagramEvent
   */
  function handleDiagramEvent(e: go.DiagramEvent) {
    const name = e.name;
    switch (name) {
      case "ChangedSelection": {
        const nodeData = e.subject.first()?.data;
        nodeData && setSelectedNodeData(nodeData);
        break;
      }
      default:
        break;
    }
  }

  /**
   * Handle GoJS model changes, which output an object of data changes via Model.toIncrementalData.
   * This method should iterates over those changes and update state to keep in sync with the GoJS model.
   * This can be done via setState in React or another preferred state management method.
   * @param obj a JSON-formatted string
   */
  function handleModelChange(obj: go.IncrementalData) {
    const insertedNodeKeys = obj.insertedNodeKeys;
    const modifiedNodeData = obj.modifiedNodeData;
    const removedNodeKeys = obj.removedNodeKeys;
    const insertedLinkKeys = obj.insertedLinkKeys;
    const modifiedLinkData = obj.modifiedLinkData;
    const removedLinkKeys = obj.removedLinkKeys;
    const modifiedModelData = obj.modelData;

    console.log(obj);

    // see gojs-react-basic for an example model change handler
    // when setting state, be sure to set skipsDiagramUpdate: true since GoJS already has this update
  }

  /**
   * Handle changes to the checkbox on whether to allow relinking.
   * @param e a change event from the checkbox
   */
  function handleRelinkChange(e: any) {
    const target = e.target;
    const value = target.checked;
    setModelData({ canRelink: value });
    setSkipsDiagramUpdate(false);
  }

  function handleDiagramClear() {
    setNodeDataArray([
      {
        key: 0,
        text: "Epsilon*",
        color: "lightblue",
        loc: nodeDataArray[0].loc,
      },
      { key: 1, text: "Zeta*", color: "orange", loc: nodeDataArray[1].loc },
      { key: 2, text: "Eta*", color: "lightgreen", loc: nodeDataArray[2].loc },
      { key: 3, text: "Theta*", color: "pink", loc: nodeDataArray[3].loc },
    ]);

    setLinkDataArray([
      { key: -1, from: 0, to: 1 },
      { key: -2, from: 0, to: 2 },
      { key: -3, from: 1, to: 1 },
      { key: -4, from: 2, to: 3 },
      { key: -5, from: 3, to: 0 },
    ]);

    setSkipsDiagramUpdate(false);
  }

  useEffect(() => {}, []);

  function render() {
    const lastLocation = selectedNodeData?.loc?.split(" ") ?? [];
    lastLocation[0] = `x: ${lastLocation[0] ?? "?"}, `;
    lastLocation[1] = `y: ${lastLocation[1] ?? "?"}`;

    const selKey = <small>KEY: {selectedNodeData?.key ?? "?"}</small>;
    const selText = <small>TEXT: {selectedNodeData.text ?? "?"}</small>;
    const selLoc = <p>LOC: {lastLocation}</p>;

    return (
      <div className="App">
        <div>
          <label>
            Allow Relinking?
            <input
              type="checkbox"
              id="relink"
              checked={modelData.canRelink}
              onChange={handleRelinkChange}
            />
          </label>
          <div>{[selKey, selText, selLoc]}</div>

          <DiagramWrapper
            ref={(ref) => setDiagramRef(ref)}
            nodeDataArray={nodeDataArray}
            linkDataArray={linkDataArray}
            modelData={modelData}
            skipsDiagramUpdate={skipsDiagramUpdate}
            onDiagramEvent={handleDiagramEvent}
            onModelChange={handleModelChange}
            onClear={handleDiagramClear}
          />
        </div>
      </div>
    );
  }

  return render();
}

export default App;
