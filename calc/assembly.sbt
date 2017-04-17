assemblyJarName in assembly := "maxCalc_0.1.2.jar"

test in assembly := {}


assemblyMergeStrategy in assembly := {
	case PathList("akka", "stream", xs @ _*) => MergeStrategy.first
	case PathList("scala", "annotation", xs @ _*) => MergeStrategy.first
	case PathList("scala", "beans", xs @ _*) => MergeStrategy.first
	case PathList("scala", "collection", xs @ _*) => MergeStrategy.first
	case PathList("scala", "compat", xs @ _*) => MergeStrategy.first
	case PathList("scala", "concurrent", xs @ _*) => MergeStrategy.first
	case PathList("scala", "io", xs @ _*) => MergeStrategy.first
	case PathList("scala", "math", xs @ _*) => MergeStrategy.first
	case PathList("scala", "ref", xs @ _*) => MergeStrategy.first
	case PathList("scala", "reflect", xs @ _*) => MergeStrategy.first
	case PathList("scala", "runtime", xs @ _*) => MergeStrategy.first
	case PathList("scala", "sys", xs @ _*) => MergeStrategy.first
	case PathList("scala", "text", xs @ _*) => MergeStrategy.first
	case PathList("scala", "util", xs @ _*) => MergeStrategy.first
	case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
	case PathList("org", "joda", xs @ _*) => MergeStrategy.first
	case "library.properties" => MergeStrategy.concat
	case PathList(ps @ _*) if ps.last endsWith "Predef$any2stringadd$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Array$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "DeprecatedPredef$class.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Enumeration$ValueOrdering$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Enumeration$ValueSet$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Enumeration.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "LowPriorityImplicits$$anon$4.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "LowPriorityImplicits.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$anon$1.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$anon$2.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$anon$3.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$eq$colon$eq$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$less$colon$less.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$ArrayCharSequence.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$ArrowAssoc$" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$ArrowAssoc.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$DummyImplicit$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$$eq$colon$eq.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$ArrowAssoc$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$DummyImplicit.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$Ensuring$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$Ensuring.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$Pair$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$RichException$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$RichException.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$SeqCharSequence.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$StringAdd$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$StringAdd.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$StringFormat$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$StringFormat.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$Triple$.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Predef$any2stringadd.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "ILoggerFactory.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "IMarkerFactory.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Logger.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "LoggerFactory.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "MDC.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "Marker.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "MarkerFactory.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "BasicMDCAdapter.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "BasicMarker.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "BasicMarkerFactory.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "FormattingTuple.class" => MergeStrategy.first
	case PathList(ps @ _*) if ps.last endsWith "MarkerIgnoringBase.class" => MergeStrategy.first


	case x =>
		val oldStrategy = (assemblyMergeStrategy in assembly).value
		oldStrategy(x)
}